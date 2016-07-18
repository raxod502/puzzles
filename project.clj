(defproject raxod502/puzzles "0.1.0-SNAPSHOT"
  :description "Solvers for KenKen and Sudoku puzzles."

  :dependencies [;; Language
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.93"]

                 ;; Server
                 [compojure "1.5.1"]
                 [environ "1.0.3"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.5.0"]

                 ;; Client
                 [reagent "0.5.1"]

                 ;; Emacs integration
                 [com.cemerick/piggieback "0.2.1"]
                 [figwheel-sidecar "0.5.4-7"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.4-7"]]

  :cljsbuild {:builds [{:id "main"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "puzzles.web.pages.splash"
                                   :output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :asset-path "js/out"}}]}
  :figwheel {:ring-handler puzzles.web.server/site}

  :clean-targets ^{:protect false} ["figwheel_server.log" "resources/public" "target"]

  :min-lein-version "2.0.0"
  :uberjar-name "puzzles-standalone.jar"
  :profiles {:uberjar {:aot :all
                       :main puzzles.web.server
                       :hooks [leiningen.cljsbuild]}})
