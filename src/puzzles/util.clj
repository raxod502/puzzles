(ns puzzles.util
  "Utility functions.")

;;;; Math

(defn long-sqrt
  "Returns the square root of x, which must be a perfect square,
  as a long."
  [x]
  (->> x Math/sqrt (+ 0.5) long))

;;;; Collections and sequences

(defn distinct-or-empty?
  "Returns true if the collection does not contain any repeated
  elements, and false otherwise."
  [coll]
  (or (empty? coll)
      (apply distinct? coll)))

(defn drop-first
  "Returns a lazy sequence of coll where the first occurrence of x
  is missing. If coll does not contain x then just returns a lazy
  sequence of coll."
  [x coll]
  (lazy-seq
    (if-let [s (seq coll)]
      (if (= x (first s))
        (rest s)
        (cons (first s)
              (drop-first x (rest s)))))))

(defn first-index
  "Returns the index of the first item in coll for which
  (pred item) returns logical true, or nil if there is no
  such item."
  [pred coll]
  (first
    (keep-indexed (fn [index item]
                    (if (pred item)
                      index))
                  coll)))

(defn last-index
  "Returns the index of the last item in coll for which
  (pred item) returns logical true, or nil if there is no
  such item."
  [pred coll]
  (when-let [reverse-idx (first-index pred (reverse coll))]
    (dec (- (count coll) reverse-idx))))

;;;; Graph theory

(defn get-all-nodes
  "Returns a set of all the nodes reachable from a given starting
  node, given a function to return all the neighbors of a node.
  Graphs may have cycles and may be directed or undirected, but
  may not have nil nodes. The starting node is guaranteed to be in
  the returned set."
  [starting-node get-neighbors]
  (loop [processed-nodes #{}
         unprocessed-nodes #{starting-node}]
    (if-let [node (first unprocessed-nodes)]
      (recur (conj processed-nodes node)
             (->> (get-neighbors node)
               (remove processed-nodes)
               (apply conj unprocessed-nodes)
               (#(disj % node))))
      processed-nodes)))

(defn break-unconnected-graph
  "Returns a set of each of the disjoint parts of an unconnected
  graph (which will contain only one part, if the graph is in fact
  connected), given a collection of all the nodes in the graph and
  a function to return all the neighbors of a node. Graphs may have
  cycles, but may not have nil nodes and must be undirected. Every
  element of nodes is guaranteed to be in exactly one of the returned
  sets."
  [nodes get-neighbors]
  (loop [unprocessed-nodes nodes
         parts #{}]
    (if-let [node (first unprocessed-nodes)]
      (let [part (get-all-nodes node get-neighbors)]
        (recur (remove part unprocessed-nodes)
               (conj parts part)))
      parts)))
