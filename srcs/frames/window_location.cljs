(ns frames.window-location
  (:refer-clojure :exclude [get set!])
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(defn parse-querystring [s]
  (-> (str/replace s #"^\?" "")
      (str/split #"&")
      (->>
       (reduce (fn [acc kv]
                 (let [[k v] (str/split kv #"=" 2)]
                   (assoc acc (keyword k) (js/decodeURIComponent v))))
               {}))))

(defn gen-query-string [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" (js/encodeURIComponent v))))
       (str/join "&")
       (str "?")))

(defn get-location []
  (let [loc    (.. js/window -location)
        href   (.. loc -href)
        search (.. loc -search)]
    {:href href
     :query-string (parse-querystring search)
     :url (first (str/split href #"#"))
     :hash (str/replace (or (.. loc -hash) "") #"^#" "")
     :host  (.. loc -host)
     :origin (.. loc -origin)
     :protocol (.. loc -protocol)
     :hostname (.. loc -hostname)
     :search search}))

(defn window-location [coef & opts]
  (assoc coef :location (get-location)))

(rf/reg-cofx :window-location window-location)
