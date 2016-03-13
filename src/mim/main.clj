(ns mim.main
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            reloaded.repl))

(defn init
  ([] (init nil))
  ([opts]
   (require 'mim.server)
   ((resolve 'mim.server/new-system) opts)))

(defn setup-app! [opts]
  (reloaded.repl/set-init! #(init opts)))
