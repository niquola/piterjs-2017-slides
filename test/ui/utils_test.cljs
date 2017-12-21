(ns ui.utils-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [ui.utils :as utils]))

(enable-console-print!)

(deftest test-tz-formatter
  (let [timezone "Europe/Moscow"
        f (partial utils/tz-n-format timezone nil)
        offset (.. (js/moment.) (tz timezone) (format "Z"))]
    (is (= (f "2017-11-20T18:00:00") (str "2017-11-20T18:00:00" offset)))
    (is (= (f "2017-11-20T15:00:00Z") (str "2017-11-20T18:00:00" offset)))
    (is (= (f "2017-11-20T10:00:00-05:00") (str "2017-11-20T18:00:00" offset)))))

(comment
  (run-tests))
