(ns aoc2021.day12_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def example-input "start-A\nstart-b\nA-c\nA-b\nb-d\nA-end\nb-end")
(def puzzle-input (slurp (io/resource "day12.txt")))

(defn make-graph []
  {:nodes {}})

(defn add-edge [graph a b]
  (let [n1 (get (:nodes graph) a {:value a :adj (set [])})
        n2 (get (:nodes graph) b {:value b :adj (set [])})
        n1 (update n1 :adj #(conj % b))
        n2 (update n2 :adj #(conj % a))]
    (update graph :nodes #(assoc % a n1 b n2))
    ))

(defn get-node [graph value]
  (get (:nodes graph) value))

(defn upper-case? [s]
  (= s (str/upper-case s)))

(defn paths [graph max value path]
  (let [small? (not (upper-case? value))
        visited? (some #(= % value) path)
        max (if (and small? visited?) (dec max) max)]
    (if (and small? visited? (or (= max 0) (= value "start")))
      []
      (let [path (conj path value)
            node (get-node graph value)
            adj (:adj node)]
        (if (= value "end")
          [path]
          (mapcat #(paths graph max % path) adj)
          ))
      )))

(defn parse [input]
  (reduce (fn [graph line] (let [[a b] (str/split line #"-")] (add-edge graph a b)))
          (make-graph)
          (str/split-lines input))
  )

(defn solve1 [input]
  (count (paths (parse input) 1 "start" [])))

(deftest solve1-test
  (testing "solve1"
    (is (= 10 (solve1 example-input)))
    (is (= 4885 (solve1 puzzle-input)))
    ))

(defn solve2 [input]
  (count (paths (parse input) 2 "start" [])))

(deftest solve2-test
  (testing "solve2"
    (is (= 36 (solve2 example-input)))
    (is (= 117095 (solve2 puzzle-input)))
    ))