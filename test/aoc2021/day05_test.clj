(ns aoc2021.day05_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzle-input (slurp (io/resource "day05.txt")))

(def example-input "0,9 -> 5,9\n8,0 -> 0,8\n9,4 -> 3,4\n2,2 -> 2,1\n7,0 -> 7,4\n6,4 -> 2,0\n0,9 -> 2,9\n3,4 -> 1,4\n0,0 -> 8,8\n5,5 -> 8,2")

(defn parse-line [line]
  (let [[_ x1 y1 x2 y2] (re-find #"(\d+),(\d+) -> (\d+),(\d+)" line)]
    [[(read-string x1) (read-string y1)] [(read-string x2) (read-string y2)]]
    ))

(defn parse [input]
  (let [lines (str/split-lines input)
        lines (map parse-line lines)]
    lines
    ))

(def example-lines (parse example-input))

(defn cover-points [[x1 y1] [x2 y2]]
  (let [[x1 x2] (sort [x1 x2])
        [y1 y2] (sort [y1 y2])]
    (for [x (range x1 (inc x2)) y (range y1 (inc y2))] [x y])
    ))

(deftest cover-points-test
  (testing "cover points"
    (is (= (cover-points [1 1] [1 3]) [[1 1] [1 2] [1 3]]))
    (is (= (cover-points [9 7] [7 7]) [[7 7] [8 7] [9 7]]))
    ))

(defn solve-1 [lines]
  (let [vh-lines (filter (fn [[[x1 y1] [x2 y2]]] (or (= x1 x2) (= y1 y2))) lines)
        vh-points (map (fn [[[x1 y1] [x2 y2]]] (cover-points [x1 y1] [x2 y2])) vh-lines)
        vh-points (reduce concat vh-points)
        overlap-count (count (filter #(<= 2 %) (vals (frequencies vh-points))))]
    overlap-count
    ))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 5 (solve-1 example-lines)))
    (is (= 8350 (solve-1 (parse puzzle-input))))
    ))

(defn diag-cover-points [[x1 y1 :as p1] [x2 y2 :as p2]]
  (let [xmin (min x1 x2)
        xmax (max x1 x2)
        ymin (min y1 y2)
        ymax (max y1 y2)
        dx (- x2 x1)
        dx (if (neg? dx) -1 1)
        dy (- y2 y1)
        dy (if (neg? dy) -1 1)]
    (loop [points [p1]]
      (let [[lx ly :as lp] (last points)]
        (cond
          (= lp p2) points
          (or (< lx xmin) (< ly ymin) (> lx xmax) (> ly ymax)) []
          :else (recur (conj points [(+ lx dx) (+ ly dy)])))
        ))))

(deftest diag-cover-points-test
  (testing "diag cover points"
    (is (= (diag-cover-points [0 1] [1 1]) []))
    (is (= (diag-cover-points [0 0] [1 9]) []))
    (is (= (diag-cover-points [0 9] [1 0]) []))

    (is (= (diag-cover-points [1 1] [3 3]) [[1 1] [2 2] [3 3]]))
    (is (= (diag-cover-points [9 7] [7 9]) [[9 7] [8 8] [7 9]]))
    (is (= (diag-cover-points [0 0] [9 9]) [[0 0] [1 1] [2 2] [3 3] [4 4] [5 5] [6 6] [7 7] [8 8] [9 9]]))
    ))
;
(defn solve-2 [lines]
  (let [diag-points (map (fn [[[x1 y1] [x2 y2]]] (diag-cover-points [x1 y1] [x2 y2])) lines)
        diag-points (reduce concat diag-points)
        vh-lines (filter (fn [[[x1 y1] [x2 y2]]] (or (= x1 x2) (= y1 y2))) lines)
        vh-points (map (fn [[[x1 y1] [x2 y2]]] (cover-points [x1 y1] [x2 y2])) vh-lines)
        vh-points (reduce concat vh-points)
        all-points (concat diag-points vh-points)
        overlap-count (count (filter #(<= 2 %) (vals (frequencies all-points))))]
    overlap-count
    ))

(deftest solve-2-test
  (testing "solve-2"
    (is (= 12 (solve-2 example-lines)))
    (is (= 19374 (solve-2 (parse puzzle-input))))
    ))