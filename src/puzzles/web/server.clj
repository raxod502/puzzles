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
   [:head
    [:title "Puzzles"]]
   [:body
    [:div#app]
    (include-js "js/main.js")]])

(def not-found-page
  [:html
   [:head
    [:title "404 Not Found"]]
   [:body
    [:h1 "Page not found"]]])

(defroutes app
  (GET "/" [] (hiccup/html main-page))
  (route/resources "/")
  (route/not-found (hiccup/html not-found-page)))

(def site (handler/site app))

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
      (jetty/run-jetty #'site
                       {:port (Long. (or port (env :port) 5000))
                        :join? false}))))

(defn -main
  [& [port]]
  (start port))
