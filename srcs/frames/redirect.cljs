(ns frames.redirect
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(defn page-redirect [url]
  (set! (.-href (.-location js/window)) url))

(defn redirect [url]
  (set! (.-hash (.-location js/window)) url))

(rf/reg-fx
 ::redirect
 (fn [opts]
   (redirect (str (:uri opts)
                       (when-let [params (:params opts)]
                         (->> params
                              (map (fn [[k v]] (str (name k) "=" (js/encodeURIComponent v))))
                              (str/join "&")
                              (str "?")))))))

(rf/reg-fx
 ::page-redirect
 (fn [opts]
   (page-redirect (str (:uri opts)
                       (when-let [params (:params opts)]
                         (->> params
                              (map (fn [[k v]] (str (name k) "=" (js/encodeURIComponent v))))
                              (str/join "&")
                              (str "?")))))))


(rf/reg-event-fx
 ::merge-params
 (fn [{db :db} [_ params]]
   (let [pth (get db :fragment-path)
         nil-keys (reduce (fn [acc [k v]]
                            (if (nil? v) (conj acc k) acc)) [] params)
         old-params (or (get-in db [:fragment-params :params]) {})]
     {::redirect {:uri pth
                  :params (apply dissoc (merge old-params params)
                                 nil-keys)}})))
