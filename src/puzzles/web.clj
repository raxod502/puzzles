(ns puzzles.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]))

(defroutes app
  (GET "/" [] "<h1>Hello from Compojure!</h1>")
  (ANY "*" [] (route/not-found "<h1>Page not found</h1>")))

(defonce ^:dynamic server nil)

(defn stop
  []
  (if server
    (.stop server)))

(defn start
  []
  (stop)
  (alter-var-root
    #'server
    (constantly
      (jetty/run-jetty (handler/site #'app)
                       {:port 5812 :join? false}))))

(defn -main
  [& args]
  (start))
