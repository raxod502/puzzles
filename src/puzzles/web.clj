(ns puzzles.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]))

(defroutes app
  (GET "/" [] "<h1>Hello from Compojure!</h1>")
  (ANY "*" [] (route/not-found "<h1>Page not found</h1>")))

(defn start-new-server
  []
  (jetty/run-jetty (handler/site #'app)
                   {:port 5812 :join? false}))

(defonce ^:dynamic server (start-new-server))

(defn restart-server
  []
  (.stop server)
  (alter-var-root #'server (start-new-server)))
