(ns aoc2021.day11_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def example-input "5483143223\n2745854711\n5264556173\n6141336146\n6357385478\n4167524645\n2176841721\n6882881134\n4846848554\n5283751526")
(def puzzle-input (slurp (io/resource "day11.txt")))

(defn parse [input]
  (mapv #(mapv read-string (str/split % #"")) (str/split-lines input)))

(defn indices [grid]
  (for [i (range (count grid)) j (range (count (first grid)))] [i j]))

(defn get-value [grid x y]
  (nth (nth grid x) y))

(defn set-value [grid x y val]
  (vec (map-indexed
         (fn [i row] (vec
                       (map-indexed
                         (fn [j cell]
                           (if (and (= x i) (= y j))
                             (if (function? val) (val cell) val)
                             cell))
                         row)))
         grid)))

(defn in-range? [grid x y]
  (let [height (count grid)
        width (count (first grid))]
    (and (>= x 0)
         (>= y 0)
         (< x height)
         (< y width))
    ))

(defn flash [{flashed :flashed grid :grid number-of-flushes :number-of-flashes :as state} x y]
  (let [already-flashed? (true? (some #(= % [x y]) flashed))
        set-grid (partial set-value grid)
        get-grid (partial get-value grid)]
    (cond
      (or already-flashed? (not (in-range? grid x y))) state
      (= 9 (get-grid x y)) (-> state
                               (assoc :grid (set-grid x y 0))
                               (assoc :flashed (conj flashed [x y]))
                               (assoc :number-of-flashes (inc number-of-flushes))
                               (flash (+ x 1) y)
                               (flash (- x 1) y)
                               (flash x (- y 1))
                               (flash x (+ y 1))
                               (flash (- x 1) (- y 1))
                               (flash (+ x 1) (- y 1))
                               (flash (- x 1) (+ y 1))
                               (flash (+ x 1) (+ y 1)))
      :else (assoc state :grid (set-grid x y inc))
      )))

(defn step [grid]
  (let [indices (indices grid)
        state {:grid grid :number-of-flashes 0 :flashed []}
        state (reduce (fn [next-state [x y]] (flash next-state x y)) state indices)
        state (dissoc state :flashed)]
    state
    ))

(defn solve1 [input]
  (let [grid (parse input)]
    (loop [i 100
           total 0
           grid grid]
      (if (= i 0)
        total
        (let [{grid :grid number-of-flashes :number-of-flashes} (step grid)
              total (+ total number-of-flashes)]
          (recur (dec i) total grid))))))


(deftest step-test
  (let [input [[1 1 1 1 1]
               [1 9 9 9 1]
               [1 9 1 9 1]
               [1 9 9 9 1]
               [1 1 1 1 1]]
        expected [[3 4 5 4 3]
                  [4 0 0 0 4]
                  [5 0 0 0 5]
                  [4 0 0 0 4]
                  [3 4 5 4 3]]]
    (testing "step"
      (is (= {:grid expected :number-of-flashes 9} (step input))))))

(deftest solve1-test
  (testing "solve1"
    (is (= 1656 (solve1 example-input)))
    (is (= 1591 (solve1 puzzle-input)))
    ))

(defn all-flashed? [grid]
  (every? #(= % 0) (flatten grid)))

(defn solve2 [input]
  (let [grid (parse input)]
    (loop [i 0
           grid grid]
      (if (all-flashed? grid)
        i
        (let [{grid :grid} (step grid)]
          (recur (inc i) grid))))))

(deftest solve2-test
  (testing "solve2"
    (is (= 195 (solve2 example-input)))
    (is (= 314 (solve2 puzzle-input)))
    ))