(ns mim.components.counter
  (:require [om.core :as om]
            #?(:clj [mim.emit.sablono :as s :refer-macros [html]]
               :cljs [mim.emit.sablono :as s :refer-macros [html]])))

(def init-state {:c 0})

#?(:cljs
   (defn counter [{:keys [c] :as data} owner]
     (reify
       om/IRender
       (render [_]
         (html
          [:div.counter
           [:div "The count is: " c]
           [:button {:onClick (fn [_] (om/transact! data :c inc))} "+"]
           [:button {:onClick (fn [_] (om/transact! data :c dec))} "-"]]))))
   :clj
   (defn counter [{:keys [c]}]
     (html
      [:div.counter
       [:div "The count is: " c]
       [:button {} "+"]
       [:button {} "-"]])))
