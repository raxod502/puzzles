(ns puzzles.sudoku
  "Namespace for solving Sudoku puzzles. Includes functions for
  transforming Sudoku puzzles to and from printable strings, as
  well as a Sudoku solver.

  The rules of Sudoku are as follows:
  - A 9x9 grid must be filled in with numbers, with each of the
    81 cells having an integer 1-9.
  - Some of the cells are filled in initially, and these cells
    cannot be modified.
  - No row or column may have more than one of the same number.
    In other words, each row and column must have all nine of
    the allowable numbers 1-9.
  - The same rule applies to each of the nine 3x3 squares the
    Sudoku grid may be naturally divided into. That is, each of
    these 3x3 squares must have exactly one of each number.

  The internal format used for Sudoku puzzles is a vector of 81
  integers, with each integer representing the value of a cell
  in row-major order. An empty cell is indicated by a zero in the
  vector.

  All Sudoku puzzles are expected to be 9x9, and no attempt is
  made to allow for puzzles of other sizes.

  The algorithm used for solving Sudoku puzzles is a simple
  recursive backtracker, which is capable of solving most newspaper
  Sudoku puzzles but does not perform well on extremely difficult
  grids."
  (:require [clojure.string :as str]
            [puzzles.sample.sudoku :refer :all]
            [puzzles.util :as util]))

;;;; Parsing and pretty-printing

(defn sudoku->vec
  "Converts a Sudoku puzzle from a string into a vector. The
  digits 1-9 denote filled cells in row-major order, and
  any other character denotes an empty cell, except for whitespace
  characters, which are ignored."
  [string]
  (mapv #(try (Long/parseLong %)
              (catch NumberFormatException _ 0))
        (re-seq #"\S" string)))

(defn vec->sudoku
  "Converts a Sudoku puzzle from a vector into a string. Empty
  cells are indicated by periods (.), and blank rows and columns
  are inserted between the nine 3x3 groupings."
  [board]
  ;; Return nil when passed a nil argument; this allows for solve
  ;; to format its solution (which could be nil, if there is no
  ;; solution) without checking for nil.
  (when board
    (->> board
      ;; Split the board into nine rows.
      (partition 9)
      ;; Split the list of rows into three groups of three rows each.
      (partition 3)
      (map (fn [row-group]
             (->> row-group
               (map (fn [row]
                      (->> row
                        ;; Split each row into three groups of three
                        ;; cells each.
                        (partition 3)
                        (map (fn [group]
                               (->> group
                                 ;; Convert each cell into a number or
                                 ;; a period.
                                 (map #(if-not (zero? %)
                                         (str %)
                                         "."))
                                 (apply str))))
                        ;; Insert a blank column between each group of
                        ;; three columns.
                        (interpose \space)
                        (apply str))))
               (str/join \newline))))
      ;; Insert a blank row between each group of three rows.
      (interpose (apply str (repeat 11 \space)))
      (str/join \newline))))

;;;; Solving

(defn valid?
  "Returns true if the Sudoku board is valid (that is, its entries
  do not violate the rules of Sudoku), and false otherwise."
  [groups board]
  (every? (fn [cells]
            (->> cells
              (map #(nth board %))
              (remove zero?)
              util/distinct-or-empty?))
          groups))

(defn- solve*
  "Helper function for solve."
  [groups cell board]
  ;; groups - the sets of cells that must have 1-9 in them, i.e.
  ;;          rows, columns, and 3x3 squares
  ;; cell - the index of the current cell being filled in
  ;; board - the current state of the Sudoku, as a vector
  (cond
    ;; Once we reach the end, we have found a solution; return it.
    (>= cell 81)
    board
    ;; If the cell is prefilled, skip it and proceed to the next.
    (not (zero? (board cell)))
    (recur groups (inc cell) board)
    :else
    (->> (for [value (range 1 10)]
           ;; Fill in the new cell.
           (assoc board cell value))
      ;; Discard illegal moves.
      (filter (partial valid? groups))
      ;; For each allowable move, proceed to the next cell.
      (map (partial solve* groups (inc cell)))
      ;; Take the first solution found, if any.
      (some identity))))

(defn solve
  "Solves a Sudoku puzzle, taking and returning a string. Returns
  nil if there is no solution."
  [sudoku]
  ;; The algorithm used here is a fairly naive recursive backtracker.
  ;; It could easily be improved by filling in the most constrained
  ;; cells first.
  (let [rows (partition 9 (range 81))]
    (vec->sudoku
      (solve*
        (concat
          rows
          (apply map vector rows)
          (for [group-x (range 3)
                group-y (range 3)]
            (for [x (range 3)
                  y (range 3)]
              (+ (* 27 group-y)
                 (* 9 y)
                 (* 3 group-x)
                 x))))
        0
        (sudoku->vec sudoku)))))
