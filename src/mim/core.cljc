(ns mim.core
  (:require [clojure.core.match :refer [match]]))

(defprotocol Parse
  (parse [form]))

(defn leaf? [n]
  (empty? (:xs n)))

(def branch? (complement leaf?))

(extend-protocol Parse
  Object
  (parse [_] {})
  clojure.lang.PersistentVector
  (parse [v]
    (match [v]
           [[t]] {:type t :attrs {} :xs []}
           [[t (attrs :guard map?)]] {:type t :attrs attrs :xs []}
           [[t (attrs :guard map?) & xs]] {:type t :attrs attrs
                                           :xs (mapv parse xs)}
           [[t & xs]] {:type t :attrs {}
                       :xs (mapv parse xs)})))

(defprotocol Emit
  (emit [c form]))

(defrecord StringEmitter []
  Emit
  (emit [c node]
    (let [tag (name (:type node))]
      (str "<" tag ">"
           (apply str (map (partial emit c) (:xs node)))
           "</" tag ">"))))

(defn foo
  "I don't do a whole lot."
  [x]
  (= (parse [:div]) {:type :div
                     :xs []})
  (println x "Hello, World!"))
