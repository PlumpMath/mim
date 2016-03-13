(ns mim.server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]
            [mim.components.counter :as c]))


(deftemplate index-template "public/index.html"
  []
  [:#container] (enlive/html-content (c/counter c/init-state)))

(defroutes main-routes
  (GET "/" [] (index-template))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app-handler
  (-> main-routes
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
