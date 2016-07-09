(ns puzzles.util-test
  (:require [clojure.test :refer :all]
            [puzzles.util :refer :all]))

(deftest long-sqrt-test
  (is (= (long-sqrt 0) 0))
  (is (= (long-sqrt 1) 1))
  (is (= (long-sqrt 4) 2))
  (is (= (long-sqrt 9) 3))
  (is (every? (fn [x] (= x (long-sqrt (* x x))))
              (range 1000))))

(deftest distinct-or-empty-test
  (is (= (distinct-or-empty? [:a :b :c]) true))
  (is (= (distinct-or-empty? [:a :b :c :a]) false))
  (is (= (distinct-or-empty? []) true))
  (is (= (distinct-or-empty? nil) true))
  (is (= (distinct-or-empty? [1 2 nil 3]) true))
  (is (= (distinct-or-empty? [1 2 nil nil 3]) false))
  (is (= (distinct-or-empty? [[1 2] [2 1]]) true))
  (is (= (distinct-or-empty? [#{1 2} #{2 1}]) false)))

(deftest drop-first-test
  (is (= (drop-first :x [:x :a :b])
         [:a :b]))
  (is (= (drop-first :x [:x :a :b :x :c])
         [:a :b :x :c]))
  (is (= (drop-first :x [:a :b :x :c :x :d])
         [:a :b :c :x :d]))
  (is (= (drop-first nil [1 2 nil 3 nil nil])
         [1 2 3 nil nil]))
  (is (= (drop-first [1 2] [[1 2 3] nil [] [1 2] nil nil [1 2]])
         [[1 2 3] nil [] nil nil [1 2]]))
  (is (= (count (drop-first 417 (range 1000))) 999))
  (is (= (drop-first 4 [1 2 3])
         [1 2 3]))
  (is (= (drop-first nil [[nil]])
         [[nil]]))
  (is (= (drop-first :x [])
         []))
  (is (= (drop-first :x nil)
         ())))

(deftest first-index-test
  (is (= 0 (first-index odd? [1 2 4])))
  (is (= 0 (first-index odd? [1 2 4 5 6])))
  (is (= 2 (first-index odd? [10 20 7 30 7 40])))
  (is (= 2 (first-index nil? [1 2 nil 3 nil nil])))
  (is (= 3 (first-index #(= (count %) 2) [[1 2 3] nil [] [1 2] nil nil [1 2]])))
  (is (= 400 (first-index #(>= % 400) (range 1000))))
  (is (= nil (first-index #{4} [1 2 3])))
  (is (= nil (first-index nil? [[nil]])))
  (is (= nil (first-index even? [])))
  (is (= nil (first-index nil? nil))))

(deftest last-index-test
  (is (= 0 (last-index odd? [1 2 4])))
  (is (= 3 (last-index odd? [1 2 4 5 6])))
  (is (= 4 (last-index odd? [10 20 7 30 7 40])))
  (is (= 5 (last-index nil? [1 2 nil 3 nil nil])))
  (is (= 6 (last-index #(= (count %) 2) [[1 2 3] nil [] [1 2] nil nil [1 2]])))
  (is (= 999 (last-index #(>= % 400) (range 1000))))
  (is (= nil (last-index #{4} [1 2 3])))
  (is (= nil (last-index nil? [[nil]])))
  (is (= nil (last-index even? [])))
  (is (= nil (last-index nil? nil))))

(defn sample-graph
  [node]
  (case node
    :a [:a]

    :b [:c]
    :c [:b]

    :d [:e :f]
    :e [:e :f]
    :f [:f]

    :g [:h :i]
    :h [:i :j]
    :i []
    :j [:g :h]))

(deftest get-all-nodes-test
  (is (= (get-all-nodes :a sample-graph)
         #{:a}))

  (is (= (get-all-nodes :b sample-graph)
         #{:b :c}))

  (is (= (get-all-nodes :c sample-graph)
         #{:b :c}))

  (is (= (get-all-nodes :d sample-graph)
         #{:d :e :f}))

  (is (= (get-all-nodes :e sample-graph)
         #{:e :f}))

  (is (= (get-all-nodes :f sample-graph)
         #{:f}))

  (is (= (get-all-nodes :g sample-graph)
         #{:g :h :i :j}))

  (is (= (get-all-nodes :h sample-graph)
         #{:g :h :i :j}))

  (is (= (get-all-nodes :i sample-graph)
         #{:i}))

  (is (= (get-all-nodes :j sample-graph)
         #{:g :h :i :j})))

(defn sample-undirected-graph
  [node]
  (case node
    :a []

    :b [:b]

    :c [:d]
    :d [:c :d]

    :e [:f :g]
    :f [:e]
    :g [:e]

    :h [:k :i]
    :i [:h :j]
    :j [:i :k]
    :k [:j :h]))

(deftest break-unconnected-graph-test
  (is (= (break-unconnected-graph
           [:a :b :c :d :e :f :g :h :i :j :k]
           sample-undirected-graph)
         #{#{:a}
           #{:b}
           #{:c :d}
           #{:e :f :g}
           #{:h :i :j :k}})))
