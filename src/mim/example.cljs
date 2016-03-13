(ns mim.example
  (:require [om.core :as om]
            [mim.components.counter :as c]))

(enable-console-print!)

(defonce app-state (atom c/init-state))

(om/root c/counter app-state {:target (.getElementById js/document "container")})

(defn init []
  (println "Init")
  (om/root c/counter app-state
           {:target (.getElementById js/document "container")}))
