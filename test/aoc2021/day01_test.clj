(ns aoc2021.day01_test
  (:require [clojure.test :refer :all]
            [aoc2021.day01 :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))


(def input-puzzle (slurp (io/resource "day01.txt")))
(def numbers (map read-string (str/split-lines input-puzzle)))

(defn diff
  [numbers] (map - numbers (into [(first numbers)] numbers)))

(defn solve-1
  [numbers] (count (filter pos? (diff numbers))))

(deftest diff-test
  (testing "diff"
    (is (= [0] (diff [1])))
    (is (= [0 1] (diff [1 2])))
    (is (= [0 -1] (diff [1 0])))
    (is (= [0 1 1 -2] (diff [1 2 3 1])))
    ))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 1711 (solve-1 numbers)))
    ))

(defn create-sliding-window
  [numbers] (if (< (count numbers) 3)
              []
              (concat [(reduce + (take 3 numbers))] (create-sliding-window (rest numbers)))
              ))

(deftest create-sliding-window-test
  (testing "create-sliding-window"
    (is (= (create-sliding-window [1 2]) []))
    (is (= (create-sliding-window [1 2 3]) [6]))
    (is (= (create-sliding-window [1 2 3 4]) [6 9]))
    (is (= (create-sliding-window [199 200 208 210 200 207 240 269 260 263])
           [607 618 618 617 647 716 769 792]))
    ))

(defn solve-2
  [numbers] (solve-1 (create-sliding-window numbers)))

(deftest solve-2-test
  (testing "solve-2"
    (is (= (solve-2 numbers) 1743))
    ))