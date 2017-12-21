(ns ui.core
  (:require [clojure.string :as str]
            [cljsjs.react]
            [garden.core :as garden]
            [garden.units :as u]
            [garden.color :as c]
            [reagent.core :as reagent]
            [reagent.ratom :refer-macros [run! reaction]]
            [re-frame.core :as rf]
            [route-map.core :as routing]))

;; data DSL
(def default-style
  [:body {:padding (u/px 100)
          :background-color "#f1f1f1"
          :color "#555"}
   [:.app {:background-color "white"
           :padding (u/px 20)}]])

(defn style-tag [gcss]
  [:style (garden/css gcss)])

;; pure function - run anywhere
(style-tag default-style)

(def user-style
  [:.user {:position "absolute"
           :right (u/px 20)
           :padding (u/px 5)
           :top (u/px 20)
           :background-color "white"}])

;; pure component
(defn user [u]
  [:div.user
   (style-tag user-style)
   [:b (:name u) " >"]])

(user {:name "Ivan"})

(defonce state (reagent/atom {:title "My title"}))

(defn current-page []
  [:div.app (style-tag default-style)
   ;; user component
   [user {:name "Nicola"}]
   [:h3 "Hello PiterJS"]
   [:h3 (:title @state)]])

(comment
  (swap! state assoc :title "Ups")

  (def myreaction
    (reaction
     (println "Changed "@state)
     (str "Changed"  (:title @state))))


  (-add-watch
   state
   :some-key (fn [k at old-state]
               (println "State changed from " @at " to " old-state)))

  (swap! state assoc :title "Ups")

  (swap! state assoc :title (str (js/Date.)))

  (-add-watch
   state
   :some-key (fn [k at old-state]
               (println "RERENDER:"
                        (pr-str (current-page)))))


  (deref myreaction)

  )


(defn stateful-comp [props]
  (let [time (reagent/atom (js/Date.))]
    (js/setInterval #(reset! time (js/Date.)) 1000)
    (fn [props]
      [:h1.timer (str (.getSeconds @time))])))

;; (defn current-page []
;;   [:div.app (style-tag default-style)
;;    [:h3 "Hello PiterJS"]
;;    [stateful-comp {}]])


(defn input-comp [path]
  (let [val (reagent/cursor state path)
        on-change (fn [ev] (swap! state assoc-in path (.. ev -target -value)))]
    (fn [_]
      [:input {:placeholder (str path)
               :value @val
               :on-change on-change}])))

;; (defn current-page []
;;   [:div.app (style-tag default-style)
;;    [:h3 "Hello PiterJS"]
;;    [:div
;;     [:lable "Input 1: "]
;;     [input-comp [:title]]]
;;    [:br]
;;    [:div
;;     [:lable "Input 2: "]
;;     [input-comp [:title]]]
;;    [:hr]
;;    [:pre (pr-str @state)]])


;; REFRAME

(def initial-db
  {:title "Hello"
   :items [{:title "item 1"}
           {:title "item 2"}]})

(defn init-db [db [_]]
  (println "Init db")
  initial-db)

(rf/reg-event-db ::init init-db)

(defn items-sub [db _]
  (get db :items))

(rf/reg-sub :items items-sub)


;; (defn add-item [db [_ txt]]
;;   (update db :items conj {:id (str (gensym))
;;                           :title txt}))

;; (rf/reg-event-db :add-item add-item)

(rf/reg-event-db
 :on-change
 (fn [db [_ pth val]]
   (assoc-in db pth val)))

(rf/reg-sub
 :new-item
 (fn [db _]
   (get db :new-item)))

(rf/reg-sub
 :progress
 (fn [db _]
   (get db :progress)))

(defn root-cmp []
  (let [items (rf/subscribe [:items])
        new-item (rf/subscribe [:new-item])
        progress (rf/subscribe [:progress])
        on-change #(rf/dispatch [:on-change [:new-item] (.. % -target -value)])
        on-submit #(do
                     (rf/dispatch [:add-item @new-item])
                     (rf/dispatch [:on-change [:new-item] ""]))]
    (fn []
      [:div.items
       (style-tag [:.item {:border-bottom "1px solid #ddd" :padding (u/px 5)}])
       (when-let [p @progress]
         [:div.alert "Loading..."])
       (for [i @items]
         [:div.item {:key (or (:id i) (:title i))}
          (:title i)])
       [:br]
       [:input {:on-change on-change 
                :value @new-item}]
       [:button.btn.btn-success {:on-click on-submit} "Add"]
       [:br]
       [:br]
       [:pre (pr-str @items)]])))

(comment
  (rf/dispatch [:add-item "New one"])
  (rf/dispatch [:add-item "Second one"])

  (add-item initial-db [:add-item "Something else"])

  )

;; effects
(defn add-item [{db :db} [_ txt]]
  (let [item {:id (str (gensym))
              :title txt}]
    {:db (assoc db :progress "Loading")
     :xhr {:uri "https://myserver"
           :body item
           :success [:item-saved]}}))

(rf/reg-event-fx :add-item add-item)

(rf/reg-event-db :item-saved
                 (fn [db [_ item]]
                   (-> db
                       (update :items conj item)
                       (dissoc :progress))))

(defn to-json [x]
  (.stringify js/JSON (clj->js x)))

(rf/reg-fx
 :xhr
 (fn [{body :body succ :success :as opts}]
   (println "FX: fetch " (:uri opts)
            " body: " (to-json body))
   (js/setTimeout
    #(rf/dispatch (conj succ body))
    1000)))

;; (defn current-page []
;;   [:div.app
;;    (style-tag default-style)
;;    [root-cmp]])

(defn- mount-root []
  (rf/dispatch [::init])
  (reagent/render
   [current-page]
   (.getElementById js/document "app")))

(defn init! []
  (mount-root))
