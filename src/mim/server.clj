(ns mim.server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes index
  (GET "/" [] (response/file-response "resources/public/index.html"))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app-handler
  (-> index
      handler/site))

(defn start-jetty [handler port]
  (jetty/run-jetty handler {:port (Integer. port) :join? false}))

(defrecord Server [port jetty]
  component/Lifecycle
  (start [component]
    (println "Start server at port " port)
    (assoc component :jetty (start-jetty app-handler port)))
  (stop [component]
    (println "Stop server")
    (when jetty
      (.stop jetty))
    component))

(defn new-system [{:keys [port]}]
  (Server. (or port 3005) nil))
