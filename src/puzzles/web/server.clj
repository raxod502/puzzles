(ns puzzles.web.server
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.core :as hiccup]
            [hiccup.page :refer [include-js]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(def main-page
  [:html
   [:head]
   [:body
    [:div#app]
    (include-js "js/main.js")]])

(def not-found-page
  [:html
   [:head]
   [:body
    [:h1 "Page not found"]]])

(defroutes app
  (GET "/" [] (hiccup/html main-page))
  (route/resources "/")
  (route/not-found (hiccup/html not-found-page)))

(defonce ^:dynamic server nil)

(defn stop
  []
  (if server
    (.stop server)))

(defn start
  [& [port]]
  (stop)
  (alter-var-root
    #'server
    (constantly
      (jetty/run-jetty (handler/site #'app)
                       {:port (Long. (or port (env :port) 5000))
                        :join? false}))))

(defn -main
  [& [port]]
  (start port))