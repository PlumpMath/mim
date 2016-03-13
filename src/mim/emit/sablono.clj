(ns mim.emit.sablono
  (:require [mim.core :as mim]
            [sablono.compiler :as s]
            [sablono.interpreter]))

(defrecord SablonoEmitter []
  mim/Emit
  (emit [c node]
    (s/compile-html (mim/walk-ast mim/node->form node))))

(defmacro html [content]
  (mim/emit (SablonoEmitter.) (mim/parse content)))
