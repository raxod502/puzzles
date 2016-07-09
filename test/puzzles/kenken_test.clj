(ns puzzles.kenken-test
  (:require [clojure.test :refer :all]
            [puzzles.kenken :refer :all]
            [puzzles.sample.kenken :refer :all]))

(deftest solve-test
  (is (= (re-seq #"\S" (solve sample-4x4))
         (re-seq #"\S" sample-4x4-solution))
      "Solution of sample KenKen #1")

  (is (= (re-seq #"\S" (solve sample-4x4*))
         (re-seq #"\S" sample-4x4-solution))
      "Solution of sample KenKen #1 in alternate format")

  (is (= (re-seq #"\S" (solve sample-6x6))
         (re-seq #"\S" sample-6x6-solution))
      "Solution of sample KenKen #2")

  (is (= (re-seq #"\S" (solve sample-6x6*))
         (re-seq #"\S" sample-6x6-solution))
      "Solution of sample KenKen #2 in alternate format"))
