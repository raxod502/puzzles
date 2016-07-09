(ns puzzles.sudoku-test
  (:require [clojure.test :refer :all]
            [puzzles.sample.sudoku :refer :all]
            [puzzles.sudoku :refer :all]))

(deftest solve-test
  (is (= (re-seq #"\S" (solve sample-1))
         (re-seq #"\S" sample-1-solution))
      "Solution of sample Sudoku #1")

  (is (= (re-seq #"\S" (solve sample-2))
         (re-seq #"\S" sample-2-solution))
      "Solution of sample Sudoku #2"))
