(defproject puzzles "0.1.0-SNAPSHOT"
  :description "Solvers for KenKen and Sudoku puzzles."
  :dependencies [;; Language
                 [org.clojure/clojure "1.8.0"]

                 ;; Website
                 [compojure "1.5.1"]
                 [environ "1.0.3"]
                 [ring/ring-jetty-adapter "1.5.0"]]
  :main puzzles.core
  :min-lein-version "2.0.0"
  :uberjar-name "puzzles-standalone.jar"
  :profiles {:uberjar {:aot :all}})
