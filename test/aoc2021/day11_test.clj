(ns aoc2021.day11_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def example-input "5483143223\n2745854711\n5264556173\n6141336146\n6357385478\n4167524645\n2176841721\n6882881134\n4846848554\n5283751526")
(def puzzle-input (slurp (io/resource "day11.txt")))

(defn parse [input]
  (mapv #(mapv read-string (str/split % #"")) (str/split-lines input)))

(defn get-value [grid x y]
  (nth (nth grid x) y))

(defn set-value [grid x y val]
  (vec (map-indexed (fn [i row] (vec (map-indexed (fn [j cell] (if (and (= x i) (= y j)) val cell)) row))) grid)))

(defn in-range? [grid x y]
  (let [height (count grid)
        width (count (nth grid 0))]
    (and (>= x 0) (>= y 0) (< x height) (< y width))
    ))

(defn flash [{flashed :flashed grid :grid number-of-flushes :number-of-flashes :as state} x y]
  (let [already-flashed? (true? (some #(= % [x y]) flashed))]
    (cond
      (or already-flashed? (not (in-range? grid x y))) state
      (= 9 (get-value grid x y)) (let [state (assoc state :grid (set-value grid x y 0))
                                       state (assoc state :flashed (conj flashed [x y]))
                                       state (assoc state :number-of-flashes (inc number-of-flushes))
                                       coords [[1 0] [-1 0] [0 -1] [0 1] [-1 -1] [1 -1] [-1 1] [1 1]]
                                       state (reduce (fn [state, [i j]] (flash state (+ x i) (+ y j))) state coords)]
                                   state)
      :else (assoc state :grid (set-value grid x y (+ 1 (get-value grid x y))))
      )))

(defn step [grid]
  (let [height (count grid)
        width (count (nth grid 0))
        indices (for [i (range height) j (range width)] [i j])
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
  (testing "step"
    (is (= {:grid              [[3 4 5 4 3]
                                [4 0 0 0 4]
                                [5 0 0 0 5]
                                [4 0 0 0 4]
                                [3 4 5 4 3]]
            :number-of-flashes 9}
           (step [[1 1 1 1 1]
                  [1 9 9 9 1]
                  [1 9 1 9 1]
                  [1 9 9 9 1]
                  [1 1 1 1 1]])))))

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