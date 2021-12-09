(ns aoc2021.day09_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def puzzle-input (slurp (io/resource "day09.txt")))

(defn parse-line [line]
  (map read-string (str/split line #"")))

(defn parse [input]
  (let [lines (str/split-lines input)
        matrix (map parse-line lines)]
    matrix))

(def puzzle-matrix (parse puzzle-input))

(def example-matrix [[2 1 9 9 9 4 3 2 1 0]
                     [3 9 8 7 8 9 4 9 2 1]
                     [9 8 5 6 7 8 9 8 9 2]
                     [8 7 6 7 8 9 6 7 8 9]
                     [9 8 9 9 9 6 5 6 7 8]])

(defn get-value [matrix x y]
  (nth (nth (vec matrix) x) y))

(defn set-value [matrix x y val]
  (map-indexed (fn [row-idx row-val] (if (= row-idx x)
                                       (map-indexed (fn [col-idx col-val] (if (= col-idx y) val col-val)) row-val)
                                       row-val
                                       ))) matrix)

(defn adj [matrix x y]
  (let [corners [[(dec x) y] [(inc x) y] [x (dec y)] [x (inc y)]]
        height (count matrix)
        width (count (nth matrix 0))
        corners (filter (fn [[x y]] (and (>= x 0) (>= y 0) (< x height) (< y width))) corners)]
    (map (fn [[x y]] [(get-value matrix x y) x y]) corners)
    ))

(deftest adj-test
  (let [matrix [[1 2 3]
                [4 5 6]
                [7 8 9]]]
    (testing "adj"
      (is (= (set [[2 0 1]
                   [4 1 0]
                   [6 1 2]
                   [8 2 1]]) (set (adj matrix 1 1))))
      (is (= (set [[2 0 1]
                   [4 1 0]]) (set (adj matrix 0 0))))
      (is (= (set [[6 1 2]
                   [8 2 1]]) (set (adj matrix 2 2))))
      (is (= (set [[3 0 2]
                   [5 1 1]
                   [1 0 0]]) (set (adj matrix 0 1))))
      (is (= (set [[4 1 0]
                   [8 2 1]]) (set (adj matrix 2 0))))
      )))

(defn find-low-points [matrix]
  (let [low-points (for [x (range (count matrix))
                         y (range (count (nth matrix 0)))]
                     (let [current (get-value matrix x y)
                           neighbours (adj matrix x y)
                           all-neighbours-are-greater (every? (fn [[val]] (< current val)) neighbours)]
                       (if (true? all-neighbours-are-greater)
                         [current x y]
                         nil
                         )))
        low-points (filter not-empty low-points)]
    low-points))

(defn solve-1 [matrix]
  (let [low-points (find-low-points matrix)
        mins (map (fn [[val]] val) low-points)]
    (reduce + (map inc mins))
    ))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 15 (solve-1 example-matrix)))
    (is (= 458 (solve-1 puzzle-matrix)))
    ))

(defn walk-up [matrix point]
  (let [[val x y] point
        neighbours (filter (fn [[i]] (and (> i val) (not= i 9))) (adj matrix x y))]
    (if (empty? neighbours)
      [point]
      (concat [point] (mapcat #(walk-up matrix %) neighbours))
      )))

(defn find-basin [matrix point]
  (count (set (walk-up matrix point))))

(deftest find-basin-test
  (testing "find-basin"
    (is (= 3 (find-basin example-matrix [1 0 1])))
    (is (= 9 (find-basin example-matrix [0 0 9])))
    (is (= 14 (find-basin example-matrix [5 2 2])))
    (is (= 9 (find-basin example-matrix [5 4 6])))
    ))

(defn solve-2 [matrix]
  (let [low-points (find-low-points matrix)
        basins (map #(find-basin matrix %) low-points)
        basins (sort-by - basins)
        basins (take 3 basins)]
    (reduce * 1 basins)
    ))

(deftest solve-2-test
  (testing "solve-2"
    (is (= 1134 (solve-2 example-matrix)))
    (is (= 1391940 (solve-2 puzzle-matrix)))
    ))