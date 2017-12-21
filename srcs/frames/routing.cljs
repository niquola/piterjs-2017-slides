(ns frames.routing
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [clojure.set :as set]
            [route-map.core :as route-map]))

(defn dispatch-routes [_]
  (let [fragment (.. js/window -location -hash)]
    (rf/dispatch [:fragment-changed fragment])))

(rf/reg-sub-raw
 :route-map/current-route
 (fn [db _] (reaction (:route-map/current-route @db))))

;; Experimental. Probably worth using for 403 and 404 errors displaying.
(rf/reg-sub-raw
 :route-map/error
 (fn [db _] (reaction (:route-map/error @db))))

(defn contexts-diff [old-contexts new-contexts params old-params]
  (let [n-idx (into #{} new-contexts)
        o-idx (into #{} old-contexts)
        to-dispose (set/difference o-idx n-idx)
        to-dispatch (into
                     (mapv (fn [x] [x :deinit old-params]) to-dispose)
                     (mapv (fn [x] [x :init params]) new-contexts))]
    to-dispatch))

(defn parse-params [s]
  (reduce
   (fn [acc pair]
     (let [[k v] (str/split pair #"=" 2)]
       (assoc acc (keyword k) (js/decodeURIComponent v))))
   {} (str/split s "&")))

(defn parse-fragment [fragment]
  (let [[path params-str] (-> fragment
                              (str/replace #"^#" "")
                              (str/split #"\?"))
        params  (if (str/blank? params-str) {} (parse-params (or params-str "")))]
    {:path path
     :query-string params-str
     :params params}))


(rf/reg-event-fx
 :fragment-changed
 (fn [{db :db} [k fragment]]
   (let [{path :path q-params :params qs :query-string} (parse-fragment fragment)]
     (if-let [route (route-map/match [:. path] (:route-map/routes db))]
       (let [params (assoc (:params route) :params q-params)
             route {:match (:match route) :params params :parents (:parents route)}
             contexts (->> (:parents route) (mapv :context) (filterv identity))
             old-contexts (:route/context db)
             old-params (get-in db [:route-map/current-route :params])]
         {:db (assoc db
                     :fragment fragment
                     :fragment-params params
                     :fragment-path path
                     :fragment-query-string qs
                     :route/context contexts
                     :route-map/current-route route)
          :dispatch-n (contexts-diff old-contexts contexts params old-params)})
       {:db (assoc db
                   :fragment fragment :route-map/current-route nil
                   :route-map/error :not-found)}))))

(rf/reg-event-fx
 :route-map/init
 (fn [cofx [_ routes]]
   {:db (assoc (:db cofx) :route-map/routes routes)
    :history {}}))

(rf/reg-fx
 :history
 (fn [_]
   (aset js/window "onhashchange" dispatch-routes)
   (dispatch-routes nil)))

(rf/reg-fx
 :route-map/redirect
 (fn [href] (aset (.-location js/window) "hash" href)))

(defn to-query-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (str/join "&")))

(defn to-hash [opts]
  (str "#" (:path opts) "?" (to-query-params (:params opts))))

(defn make-fragment [params]
  (let [opts  (parse-fragment (.. js/window -location -hash))]
    (to-hash (assoc opts :params params))))

(rf/reg-fx
 :route-map/set-params
 (fn [params]
   (let [opts  (parse-fragment (.. js/window -location -hash))]
     (aset (.. js/window -location) "hash" (to-hash (assoc opts :params params))))))
