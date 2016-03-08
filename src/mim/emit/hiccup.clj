(ns mim.emit.hiccup
  "Emit string HTML using hiccup and mim's AST"
  (:require [mim.core :as mim]
            [hiccup.compiler :as h]))

(defrecord StringEmitter []
  mim/Emit
  (emit [c node]
    (letfn [(node->hiccup [n]
              (case (:type n)
                :static (:value n)
                :dynamic (:value n)
                :form (into [(:tag n) (:attrs n)] (:xs n))))]
      (h/compile-html (mim/walk-ast node->hiccup node)))))

(defmacro html [content]
  (mim/emit (StringEmitter.) (mim/parse content)))
