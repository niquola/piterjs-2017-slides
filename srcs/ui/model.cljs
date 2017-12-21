(ns ui.model)

;; (defn tables-sub [db _]
;;   (:tables db))

;; (rf/reg-sub :tables tables-sub)

;; (tables-sub {:tables ["ups"]} [:tables])
;; ;; (js/alert "Hello")

;; (defn tables-grid [tables]
;;   [:div
;;    (for [t tables]
;;      [:div {:key (:table_name t)}
;;       [:a {:href (str "#/tables/" (:table_name t))}
;;        [:b (:table_name t)]]])])


;; (tables-grid [{:table_name "ups"}])

;; (rf/reg-event-fx
;;  :load-tables
;;  (fn [coef _]
;;    {:xhr {:uri "http://localhost:8889/tables"
;;           :action :tables-loaded}}))

;; (js->clj #js[#js{:a "1"}] :keywordize-keys true)

;; (rf/reg-event-db
;;  :tables-loaded
;;  (fn [db [_ data]]
;;    (assoc db :tables data)))


;; (defn xhr [{uri :uri action :action}]
;;   (->
;;    (js/fetch uri)
;;    (.then (fn [res]
;;             (-> (.json res)
;;                 (.then (fn [x]
;;                          (rf/dispatch [action (js->clj x :keywordize-keys true)]))))))))

;; (rf/reg-fx :xhr xhr)

;; (defn index-page []
;;   (let [tables (rf/subscribe [:tables])]
;;     (fn []
;;       [:div "Hello"
;;        style
;;        [tables-grid @tables]
;;        [:button {:on-click #(rf/dispatch [:load-tables])} "Load!"]])))

;; (defn table-page []
;;   (fn [param]
;;     [:div "Tables" style
;;      [:pre (pr-str param)]]))

;; (rf/reg-sub :route (fn [db] (:route db)))

;; (def pages {:index index-page
;;             :table table-page})


;; (defn init-ev [coef]
;;   ;; (.log js/console "Hello")
;;   {:db {:title "Hello"
;;         :tables [{:table_name "tables"}
;;                  {:table_name "columns"}]}})

;; ;; [(rf/inject-cofx :window-location)]
;; (rf/reg-event-fx ::initialize init-ev)


;; (rf/reg-event-db
;;  :route-changed
;;  (fn [db [_ hash]] (assoc db :route hash)))

;; (def routes {:. :index
;;              "tables" {[:table] :table}})

;; (routing/match "/tables/tables" routes)

;; (defn dispatch-routes [x]
;;   (let [h (.. js/window -location -hash)
;;         h (str/replace h #"^#" "")
;;         route (routing/match h routes)]
;;     (.log js/console ">>>" h)
;;     (rf/dispatch [:route-changed route])))

;; (defn init-routes []
;;   (aset js/window "onhashchange" dispatch-routes)
;;   (dispatch-routes nil))

;; (defn current-page []
;;   (let [route (rf/subscribe [:route])]
;;     (fn []
;;       [:div
;;        (if-let [r @route]
;;          (let [p (get pages (:match r))]
;;            [p (:params r)])
;;          [:div "ups"])])))

;; (defn- mount-root []
;;   (reagent/render
;;    [current-page]
;;    (.getElementById js/document "app")))

;; (defn init! []
;;   (rf/dispatch [::initialize])
;;   (mount-root)
;;   (init-routes))




