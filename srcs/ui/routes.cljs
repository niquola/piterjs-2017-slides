(ns ui.routes
  (:require [clojure.string :as str]
            [route-map.core :as route-map]))

(def routes {:. :navigation/index
             "user" {:. :list-users
                     [:id] {:. :show-user}}})

(->
 (route-map/match "/" routes)
 :match)

(->
 (route-map/match "/ups" routes)
 :match)

(->
 (route-map/match "/user" routes)
 :match)

(->
 (route-map/match "/user/5" routes)
 (select-keys [:match :params]))


(defn to-query-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (str/join "&")))

(defn href [& parts]
  (let [params (if (map? (last parts)) (last parts) nil)
        parts (if params (butlast parts) parts)
        url (str "/" (str/join "/" (map (fn [x] (if (keyword? x) (name x) (str x))) parts)))]
    (when-not  (route-map/match [:. url] routes)
      (println (str url " is not matches routes")))
    (str "#" url (when params (str "?" (to-query-params params))))))

(href "users" "5")
(href "unexisting" "5")


