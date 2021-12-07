(ns aoc2021.day06_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def puzzle-input (slurp (io/resource "day06.txt")))
(def puzzle-timers (mapv read-string (str/split puzzle-input #",")))

(defn next-day
  ([timers] (let [existing (map #(if (= % 0) 6 (dec %)) timers)
                  new (take (count (filter zero? timers)) (repeat 8))]
              (concat existing new)))
  ([timers days] (loop [timers timers
                        days days]
                   (if (= 0 days)
                     timers
                     (recur (next-day timers) (dec days))
                     ))))

(deftest next-day-test
  (testing "next day"
    (is (= [2 3 2 0 1] (next-day [3 4 3 1 2])))
    (is (= [1 2 1 6 0 8] (next-day [2 3 2 0 1])))
    (is (= 26 (count (next-day [3 4 3 1 2] 18))))
    ))

(defn solve-1 [timers]
  (count (next-day timers 80)))

(deftest solve-1-test
  (testing "solve 1"
    (is (= 5934 (solve-1 [3 4 3 1 2])))
    (is (= 377263 (solve-1 puzzle-timers)))
    ))


(defn solve-2 [timers]
  (let [days {0 0 1 0 2 0 3 0 4 0 5 0 6 0 7 0 8 0}
        days (reduce (fn [m k] (update m k + 1)) days timers)]
    (reduce +
            (vals (reduce (fn [m k]
                            (let [today (mod k (count m))
                                  idx (mod (+ today 7) (count m))]
                              (update m idx + (get m today)))) days (range 256))))
    ))

(deftest solve-2-test
  (testing "solve 2"
    (is (= 26984457539 (solve-2 [3 4 3 1 2])))
    (is (= 1695929023803 (solve-2 puzzle-timers)))
    ))