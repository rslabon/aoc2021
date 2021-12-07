(ns aoc2021.day07_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def example-positions [16 1 2 0 4 2 7 1 2 14])
(def puzzle-input (slurp (io/resource "day07.txt")))
(def puzzle-positions (map read-string (str/split puzzle-input #",")))

(defn find-min [cost-fn positions current max current-min]
  (if (= current max)
    current-min
    (let [cost (reduce + (map #(cost-fn (Math/abs (- % current))) positions))]
      (if (< cost current-min)
        (find-min cost-fn positions (inc current) max cost)
        current-min
        ))))

(defn solve-1 [positions]
  (find-min identity positions (apply min positions) (apply max positions) Long/MAX_VALUE))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 37 (solve-1 example-positions)))
    (is (= 340056 (solve-1 puzzle-positions)))
    ))

(defn sum [n] (* (/ (+ 1 n) 2) n))

(defn solve-2 [positions]
  (find-min sum positions (apply min positions) (apply max positions) Long/MAX_VALUE)
  )

(deftest solve-2-test
  (testing "solve-2"
    (is (= 168 (solve-2 example-positions)))
    (is (= 96592275 (solve-2 puzzle-positions)))
    ))