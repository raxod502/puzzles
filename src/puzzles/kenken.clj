(ns puzzles.kenken
  "Namespace for solving KenKen puzzles. Includes functions for
  transforming KenKen puzzles to and from printable strings, as
  well as a KenKen solver.

  The rules of KenKen are as follows:
  - An nxn grid must be filled in with numbers, with each of the
    cells having an integer 1-n.
  - No row or column may have more than one of the same number.
    In other words, each row and column must have all n of the
    allowable numbers 1-n.
  - The grid is divided by heavier lines into irregularly shaped
    subgroups of cells, each of which has a number and an
    operation of basic arithmetic (+, -, *, or /). (This
    information does not occupy a cell; it is usually printed as
    a small superscript in one of the cells in the subgroup, and
    that cell must still be filled in with a number 1-n. When the
    operation shown is applied to the numbers in its subgroup,
    the superscript number must result. For subtraction and
    division, there are usually only two cells in the subgroup
    and the operation can be performed in either order.

  There are two internal formats used for KenKen puzzles. One is
  for specifying the subgroups and their operations and target
  numbers; the other is for specifying the numbers that have been
  filled in to the grid. The kenken->vec function converts from
  a string to the former format; the vec->kenken function converts
  from the latter format back to a string, and only applies to
  solved KenKen puzzles. As of yet, no other conversions have been
  implemented.

  The first internal format is a vector of maps, each of which
  corresponds to a subgroup and has the keys :operation, :target,
  and :cells. The :operation is one of the clojure.core functions
  +, -, *, or /; and the :target is the integer that must result
  upon application of the :operation to the numbers in the
  subgroup. The :cells is a vector of cell indices, in row-major
  order starting from 0 in the upper-left corner, contained in the
  subgroup.

  The second internal format is a simple vector of integers, where
  the indexing scheme is the same one used for the values of the
  :cells vectors. A nonzero integer indicates a filled cell, while
  a zero indicates an empty cell.

  The size of a KenKen puzzle is automatically detected, and can
  be any nonzero size.

  The algorithm used for solving KenKen puzzles is a simple
  recursive backtracker, which is capable of easily solving most
  newspaper puzzles."
  (:require [clojure.string :as str]
            [puzzles.sample.kenken :refer :all]
            [puzzles.util :as util]))

;;;; Parsing and pretty-printing

(defn kenken->vec
  "Converts a KenKen puzzle from a string into a vector. See
  the examples in puzzles.sample.kenken for the format, which
  must conform to the following rules (among others):
  - The numbers and operations can be specified either within
    the grid, or separately. (Although the former looks prettier,
    the latter is much faster to enter.) If the numbers and
    operations are specified separately, the two sections may
    come in either order.
  - Dashes (-) and pipes (|) must be used for horizontal and
    vertical separators, but any character may be used in place of
    the plus (+).
  - Each cell can only be one character high.
  - Cells can have any width, but they must all have the same
    width within a single puzzle.
  - If numbers and operations are being specified within the
    grid, a number and an operation in the same subgroup must
    be placed in different cells. Also, numbers and operations
    must be fully within their cells, not on the edges between
    cells.
  - A number cannot be split over multiple cells, but the cells
    can be made as wide as is necessary to accomodate the width
    of the number.
  - If numbers and operations are being specified separately
    from the grid, then the number and operation must be placed
    in the same entry. Every cell must have an entry, with empty
    cells denoted by periods (.). Entries must be separated by
    a single space each.
  - Leading and trailing whitespace is ignored on each line, and
    empty lines are also ignored, except within the grid and
    within the entry matrix (if present)."
  [string]
  (let [lines (mapv str/trim (str/split-lines string))
        ;; Find the lines that contain the grid, assuming that
        ;; the grid will be the only place pipe (|) characters
        ;; will appear.
        grid-start (dec (util/first-index #(some #{\|} %)
                                          lines))
        grid-end (+ (util/last-index #(some #{\|} %)
                                     lines)
                    2)
        grid-lines (subvec lines grid-start grid-end)
        ;; Compute the side length of the grid, assuming that
        ;; each cell and wall is only one line tall.
        size (/ (dec (count grid-lines)) 2)
        ;; Compute the internal width of a cell, assuming that
        ;; each cell has the same width and accounting for walls.
        cell-width (-> (first grid-lines)
                     count
                     dec
                     (/ size)
                     dec)
        ;; Define predicates for testing whether there is a wall
        ;; on each side of a given cell, indexed from the upper-left
        ;; corner.
        left-wall? (fn [x y]
                     (and
                       (>= x 0)
                       (<= x size)
                       (-> grid-lines
                         (nth (inc (* y 2)))
                         (nth (* x (inc cell-width)))
                         (not= \|))))
        right-wall? (fn [x y]
                      (left-wall? (inc x) y))
        top-wall? (fn [x y]
                    (and
                      (>= y 0)
                      (<= y size)
                      (-> grid-lines
                        (nth (* y 2))
                        (nth (inc (* x (inc cell-width))))
                        (not= \-))))
        bottom-wall? (fn [x y]
                       (top-wall? x (inc y)))
        ;; Define a list of all the cells in the grid.
        cells (for [x (range size)
                    y (range size)]
                [x y])
        ;; Define a function for getting all cells that are adjacent
        ;; to a given cell, are within the bounds of the grid, and
        ;; are not blocked by a wall.
        get-neighbors (fn [[x y]]
                        (cond-> #{}
                          (left-wall? x y)
                          (conj [(dec x) y])

                          (right-wall? x y)
                          (conj [(inc x) y])

                          (top-wall? x y)
                          (conj [x (dec y)])

                          (bottom-wall? x y)
                          (conj [x (inc y)])))
        ;; Treat the KenKen grid as a graph in order to break the set
        ;; of cells into the subgroups that must satisfy mathematical
        ;; constraints.
        groups (util/break-unconnected-graph cells get-neighbors)
        ;; Define a way of turning strings from {+, -, *, /} into Clojure
        ;; functions without using eval.
        parse-operation (fn [operation]
                          (->> operation
                            (symbol "clojure.core")
                            resolve
                            deref))
        ;; Extract the numbers and operations (collectively, "labels")
        ;; specified in the grid.
        grid-labels (into {}
                          (for [[x y] cells]
                            ;; Use knowledge of the cell width to extract
                            ;; the correct substring from each line.
                            (let [str-x (inc (* x (inc cell-width)))
                                  label (-> grid-lines
                                          (nth (inc (* y 2)))
                                          (subs str-x (+ str-x cell-width))
                                          str/trim)]
                              ;; Create a map entry, so that we can later
                              ;; perform lookup by cell.
                              [[x y]
                               ;; Wrap the label in a vector, because if the
                               ;; labels are specified separately from the
                               ;; grid then there can be more than one label
                               ;; per cell.
                               [(condp re-matches label
                                  #"\d+" :>> Long/parseLong
                                  #"[+\-*/]" :>> parse-operation
                                  #"" nil)]])))
        labels (if (some identity (apply concat (vals grid-labels)))
                 ;; If there was at least one label specified within the grid,
                 ;; ignore any that may have been specified separately.
                 grid-labels
                 ;; Otherwise, parse the external label specifications.
                 (let [;; Determine the range of lines that contains the
                       ;; external label specifications, assuming that the
                       ;; label specifications are the only place that numbers
                       ;; or periods (.) can appear without a pipe (|) character
                       ;; also appearing.
                       labels-start (util/first-index
                                      #(and (re-find #"[1-9.]" %)
                                            (not (some #{\|} %)))
                                      lines)
                       labels-end (+ labels-start size)
                       label-lines (subvec lines labels-start labels-end)]
                   (->> label-lines
                     (map-indexed
                       (fn [y line]
                         (map-indexed
                           (fn [x label]
                             ;; Create a map entry, so that we can later perform
                             ;; lookup by cell.
                             [[x y]
                              (condp re-matches label
                                #"\d+" :>> #(vector (Long/parseLong %))
                                #"(\d+)([+\-*/])" :>> (fn [[_ n op]]
                                                        [(Long/parseLong n)
                                                         (parse-operation op)])
                                #"\." nil)])
                           (str/split line #" "))))
                     ;; Flatten the nested list generated by the nested calls
                     ;; to map-indexed.
                     (apply concat)
                     (into {}))))
        ;; Define a function for converting (x, y) cell coordinates into the
        ;; row-major index used in the solver.
        xy->index (fn [[x y]]
                    (+ (* y size) x))]
    (->> groups
      (mapv (fn [cells]
              (reduce (fn [m cell]
                        ;; Reduce over the (possibly) multiple pieces of
                        ;; information provided by a single cell.
                        (reduce (fn [m label]
                                  (condp #(%1 %2) label
                                    fn? (assoc m :operation label)
                                    number? (assoc m :target label)
                                    nil? m))
                                (update m :cells conj (xy->index cell))
                                (get labels cell)))
                      {:operation identity
                       :cells []}
                      cells)))
      ;; Place the subgroups that are upper-left-most first, to improve
      ;; efficiency in the solver.
      (sort-by #(apply max (:cells %))))))

(defn vec->kenken
  "Converts a solved KenKen board from a vector into a string."
  [board]
  ;; Return nil when passed a nil argument; this allows for solve
  ;; to format its solution (which could be nil, if there is no
  ;; solution) without checking for nil.
  (when board
    (let [size (util/long-sqrt (count board))]
      (->> board
        (partition size)
        (map #(apply str %))
        (str/join \newline)))))

;;;; Solving

(defn valid?
  "Returns true if the KenKen board is valid (that is, its
  entries satisfy the constraints on each row, column, and
  subgroup), and false otherwise."
  [lines groups board]
  (and
    ;; Check for repetitions in a row or column.
    (every? (fn [cells]
              (->> cells
                (map #(nth board %))
                (remove zero?)
                util/distinct-or-empty?))
            lines)
    ;; Check for mathematical correctness within each group
    (every? (fn [{:keys [operation target cells]}]
              (let [values (map #(nth board %) cells)]
                (or (some zero? values)
                    (= (apply operation (reverse (sort values)))
                       target))))
            groups)))

(defn- solve*
  "Helper function for solve."
  [size lines groups cell board]
  ;; size - side length of the puzzle grid
  ;; lines - the sets of cells for each row and column
  ;; groups - a vector of maps representing the groups of cells
  ;;          with a mathematical constraint on their values
  ;; cell - the index of the current cell being filled in
  ;; board - the current state of the KenKen, as a vector
  (if (< cell (count board))
    (->> (for [value (range 1 (inc size))]
           ;; Fill in the new cell
           (assoc board cell value))
      ;; Discard illegal moves.
      (filter (partial valid? lines groups))
      ;; For each allowable move, proceed to the next cell.
      (map (partial solve* size lines groups (inc cell)))
      ;; Take the first solution found, if any.
      (some identity))
    ;; Once we reach the end, we have found a solution; return it.
    board))

(defn solve
  "Solves a KenKen puzzle, taking and returning a string."
  [kenken]
  (let [groups (kenken->vec kenken)
        size (->> groups
               (map :cells)
               (apply concat)
               (apply max)
               inc
               util/long-sqrt)
        area (* size size)
        row-lines (partition size (range area))
        column-lines (apply map vector row-lines)
        lines (concat row-lines column-lines)
        cell 0
        board (vec (repeat area 0))
        solution (solve* size lines groups cell board)]
    (vec->kenken solution)))
