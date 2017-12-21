(ns ui.intro)

"
 Clojure is JavaScript--
"

"
 1. - syntax

 (1 + 2) - 3

 just an AST

['minus',
  ['plus',
    1,
    2],
  3]

"

(- (+ 1 2) 3)

"
function declaration and application

function plus (a,b) {return a + b};

plus(1,2);

"

(defn xplus [a b]
  (+ a b))

((fn [x y] (* x y)) 1 2)


(type xplus)

(xplus 1 2)


"
we operate on AST
"

(defn mymin [a b]
  (if (a > b) b a))


"
 primitives
"

(type 1)

(type "string")

(type #"^Cl.*$")

(type (fn [x] x))


"
interop
"

#_(js/alert "Hello!")

(.-location js/window)

(.. js/window -location -href)
(.. js/window -location -host)

(let [d (js/Date.)]
  (.getFullYear d))


"
composites
"
;; hashmap
(def user {:name "niquola"
           :address {:city "SPb"}
           :profiles [{:type "github"
                       :link "https://....."}
                      {:type "twitter"
                       :link "https://....."}]
           :age 37})

(type user)

;; immutability
(assoc user :attr "value")

user

;; equality
(= {:a 1} {:a 1})

;; a lot of supporting functions

(get-in user [:address :city])

(assoc-in user [:address :city] "LA")

(update-in user [:profiles 0 :link] (fn [old] (str old "+++++")))

(select-keys user [:name :address])


;; vector

(def clojurists [{:name "Rich"}
                 {:name "Michael"}])

(first clojurists)
(second clojurists)

(conj clojurists {:name "Your name"})

(map :name clojurists)

(butlast clojurists)

(last clojurists)

(rest clojurists)

(get-in clojurists [0 :name])

;; destructiring

(let [[x & xs] [1 2 3 4]]
  {:x x :xs xs})

(let [{a :a b :b :as m} {:a 1, :b 2}]
  [a b m])

(let [{[{b :b} _] :a} {:a [{:b 1} {:b 2}]}]
  b)

(defn say [{name :name :as u}]
  (str "Hello, " name))

(say {:name "Piter"})
