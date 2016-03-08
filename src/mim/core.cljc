(ns mim.core
  (:require [clojure.core.match :refer [match]]
            [sablono.compiler :as s]))

(defprotocol Parse
  (parse [form]))

(defn leaf? [n]
  (empty? (:xs n)))

(def branch? (complement leaf?))

;; Need to parse special sugar CSS for id and class, ex: div#id.class
;; from hiccup.compiler

(def ^{:doc "Regular expression that parses a CSS-style id and class from an element name."
       :private true}
  re-tag #"([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?")

(defn tag->css [tag]
  (let [[_ tag id class] (re-matches re-tag (name tag))]
    [tag {:id id :class (if class (.replace ^String class "." " "))}]))

(extend-protocol Parse
  String
  (parse [s] {:type :static :value s})
  clojure.lang.Keyword
  (parse [k] {:type :static :value k})
  clojure.lang.Symbol
  (parse [s] {:type :dynamic :value s})
  clojure.lang.PersistentList
  (parse [l]
    {:type :dynamic :value l})
  clojure.lang.PersistentVector
  (parse [v]
    (merge {:type :form}
           (match [v]
                  [[t]] {:tag t :attrs {} :xs []}
                  [[t (attrs :guard map?)]] {:tag t :attrs attrs :xs []}
                  [[t (attrs :guard map?) & xs]] {:tag t :attrs attrs
                                                  :xs (mapv parse xs)}
                  [[t (attrs :guard symbol?) & xs]] {:tag t :attrs attrs
                                                     :xs (mapv parse xs)}
                  [[t (attrs :guard list?) & xs]] {:tag t :attrs attrs
                                                   :xs (mapv parse xs)}
                  [[t & xs]] {:tag t :attrs {}
                              :xs (mapv parse xs)}))))

(defn walk-ast [f node]
  {:pre [(fn? f)]}
  (if (leaf? node)
    (f node)
    (f (update node :xs (partial mapv (partial walk-ast f))))))

(defn node->form [n]
  (case (:type n)
    :static (:value n)
    :dynamic (:value n)
    :form (into [(:tag n) (:attrs n)] (:xs n))))

;; ======================================================================
;; Emit

(defprotocol Emit
  (emit [c form]))

;; Sablono emitter

(defrecord SablonoEmitter []
  Emit
  (emit [c node]
    (s/compile-html (walk-ast node->form node))))

(defmacro html [content]
  (emit (SablonoEmitter.) (parse content)))
