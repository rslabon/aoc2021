(ns aoc2021.day04_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

(def puzzle-input (slurp (io/resource "day04.txt")))

(defn parse-line [line]
  (let [line-words (filter not-empty (str/split line #"\s+"))]
    (mapv read-string line-words)))

(defn parse [input]
  (let [lines (str/split input #"\n\n")
        input-numbers (first lines)
        input-numbers (str/split input-numbers #",")
        input-numbers (mapv read-string input-numbers)
        input-boards (rest lines)
        input-boards (mapv (fn [board] (mapv parse-line (str/split-lines board))) input-boards)]
    [input-numbers input-boards]))

(def example-numbers [7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1])
(def example-boards
  [
   [[22 13 17 11 0]
    [8 2 23 4 24]
    [21 9 14 16 7]
    [6 10 3 18 5]
    [1 12 20 15 19]]

   [[3 15 0 2 22]
    [9 18 13 17 5]
    [19 8 7 25 23]
    [20 11 10 24 4]
    [14 21 16 12 6]]

   [[14 21 17 24 4]
    [10 16 15 9 19]
    [18 8 23 26 20]
    [22 11 13 6 5]
    [2 0 12 3 7]]
   ])

(defn mark [board number]
  (mapv (fn [row] (mapv (fn [cell] (if (= cell number) :x cell)) row)) board))

(deftest mark-test
  (testing "mark"
    (is (= [[22 13 17 :x 0]
            [8 2 23 4 24]
            [21 9 14 16 7]
            [6 10 3 18 5]
            [1 12 20 15 19]] (mark [[22 13 17 11 0]
                                    [8 2 23 4 24]
                                    [21 9 14 16 7]
                                    [6 10 3 18 5]
                                    [1 12 20 15 19]] 11)))
    (is (= [[22 13 17 11 0]
            [8 2 23 4 24]
            [21 9 14 16 7]
            [6 10 3 18 5]
            [1 12 20 15 19]] (mark [[22 13 17 11 0]
                                    [8 2 23 4 24]
                                    [21 9 14 16 7]
                                    [6 10 3 18 5]
                                    [1 12 20 15 19]] 29)))))


(defn wins? [board]
  (let [row-wins? (true? (some (fn [row] (every? (fn [cell] (= :x cell)) row)) board))
        rlen (count board)
        clen (count (first board))
        coll-wins? (true? (some (fn [coll-idx] (every? (fn [row-idx] (= :x (nth (nth board row-idx) coll-idx))) (range 0 rlen))) (range 0 clen)))]
    (or row-wins? coll-wins?)))

(deftest wins?-test
  (testing "wins?"
    (is (= true (wins? [[:x :x :x :x :x]
                        [10 16 15 :x 19]
                        [18 8 :x 26 20]
                        [22 :x 13 6 :x]
                        [:x :x 12 3 :x]])))
    (is (= true (wins? [[:x 13 17 11 0]
                        [:x 2 23 4 24]
                        [:x 9 14 16 7]
                        [:x 10 3 18 5]
                        [:x 12 20 15 19]])))
    (is (= true (wins? [[22 13 17 11 :x]
                        [8 2 23 4 :x]
                        [21 9 14 16 :x]
                        [6 10 3 18 :x]
                        [1 12 20 15 :x]])))
    (is (= false (wins? [[22 13 17 11 0]
                         [8 2 23 4 24]
                         [21 9 14 16 7]
                         [6 10 3 18 5]
                         [1 12 20 15 19]])))
    (is (= true (wins? [[:x :x :x :x :x]
                        [8 2 23 4 24]
                        [21 9 14 16 7]
                        [6 10 3 18 5]
                        [1 12 20 15 19]])))
    (is (= true (wins? [[22 13 17 11 0]
                        [8 2 23 4 24]
                        [21 9 14 16 7]
                        [:x :x :x :x :x]
                        [1 12 20 15 19]])))))

(defn sum-of-unmarked [board]
  (reduce + (filter #(not= :x %) (flatten board))))

(deftest sum-of-unmarked-test
  (testing "sum of unmarked"
    (is (= 8 (sum-of-unmarked [[:x 2 3]
                               [1 1 1]])))))

(defn solve-1 [boards numbers]
  (let [number (first numbers)
        new-boards (mapv #(mark % number) boards)
        wins-boards (filter #(wins? %) new-boards)
        done? (not-empty wins-boards)]
    (if done?
      (* number (reduce + (mapv #(sum-of-unmarked %) wins-boards)))
      (solve-1 new-boards (rest numbers))
      )))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 4512 (solve-1 example-boards example-numbers)))
    (let [[numbers boards] (parse puzzle-input)]
      (is (= 12796 (solve-1 boards numbers))))
    ))

(defn solve-2 [boards numbers last-win-sum last-win-number last-win-boards-idx]
  (let [number (first numbers)
        new-boards (mapv #(mark % number) boards)
        wins (map-indexed #(if (wins? %2) %1 nil) new-boards)
        wins-boards-idx (filter #(not= nil %) wins)
        new-win-number? (not= last-win-boards-idx wins-boards-idx)
        last-win-number (if new-win-number? number last-win-number)
        new-wins-boards (mapv #(nth new-boards %) (vec (set/difference (set wins-boards-idx) (set last-win-boards-idx))))
        last-win-sum (if new-win-number? (reduce + (mapv #(sum-of-unmarked %) new-wins-boards)) last-win-sum)]
    (if (empty? (rest numbers))
      (* last-win-sum last-win-number)
      (solve-2 new-boards (rest numbers) last-win-sum last-win-number wins-boards-idx)
      )))

(deftest solve-2-test
  (testing "solve-2"
    (is (= 1924 (solve-2 example-boards example-numbers nil nil [])))
    (let [[numbers boards] (parse puzzle-input)]
      (is (= 18063 (solve-2 boards numbers nil nil [])))
      )))