(ns aoc2021.day03_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def puzzle-input (slurp (io/resource "day03.txt")))
(def puzzle-bin-numbers (vec (mapv #(mapv read-string (str/split % #"")) (str/split-lines puzzle-input))))

(def example-bin-numbers [[0 0 1 0 0]
                          [1 1 1 1 0]
                          [1 0 1 1 0]
                          [1 0 1 1 1]
                          [1 0 1 0 1]
                          [0 1 1 1 1]
                          [0 0 1 1 1]
                          [1 1 1 0 0]
                          [1 0 0 0 0]
                          [1 1 0 0 1]
                          [0 0 0 1 0]
                          [0 1 0 1 0]])

(defn bin-array-to-dec [xs]
  (Integer/parseInt (str/join "" xs) 2))

(defn transpose-2d-array [xs]
  (let [rlen (count xs)
        clen (count (first xs))]
    (mapv (fn [col] (mapv (fn [row] (get (get xs row) col)) (range 0 rlen))) (range 0 clen))))

(defn find-power [bin-numbers]
  (let [tbin-numbers (transpose-2d-array bin-numbers)
        tbin-freqs (mapv frequencies tbin-numbers)
        most-common (mapv #(let [zero-freq (get % 0)
                                 one-freq (get % 1)]
                             (if (> one-freq zero-freq)
                               1
                               0
                               )) tbin-freqs)
        least-common (mapv #(let [zero-freq (get % 0)
                                  one-freq (get % 1)]
                              (if (< one-freq zero-freq)
                                1
                                0
                                )) tbin-freqs)
        gamma (bin-array-to-dec most-common)
        epsilon (bin-array-to-dec least-common)]
    (* gamma epsilon)))

(deftest find-power-test
  (testing "find-power"
    (is (= 198 (find-power example-bin-numbers)))
    (is (= 775304 (find-power puzzle-bin-numbers)))
    ))

(defn oxygen
  ([bin-numbers] (oxygen bin-numbers 0))
  ([bin-numbers idx] (if (= 1 (count bin-numbers))
                       (bin-array-to-dec (first bin-numbers))
                       (let [tbin-numbers (transpose-2d-array bin-numbers)
                             tbin-freq (frequencies (nth tbin-numbers idx))
                             most-common (let [zero-freq (get tbin-freq 0)
                                               one-freq (get tbin-freq 1)]
                                           (if (>= one-freq zero-freq)
                                             1
                                             0
                                             ))
                             ]
                         (oxygen (filterv #(= (nth % idx) most-common) bin-numbers) (+ 1 idx))))))

(deftest oxygen-test
  (testing "oxygen"
    (is (= 23 (oxygen example-bin-numbers)))
    ))

(defn co2
  ([bin-numbers] (co2 bin-numbers 0))
  ([bin-numbers idx] (if (= 1 (count bin-numbers))
                       (bin-array-to-dec (first bin-numbers))
                       (let [tbin-numbers (transpose-2d-array bin-numbers)
                             tbin-freq (frequencies (nth tbin-numbers idx))
                             least-common (let [zero-freq (get tbin-freq 0)
                                                one-freq (get tbin-freq 1)]
                                            (if (< one-freq zero-freq)
                                              1
                                              0
                                              ))
                             ]
                         (co2 (filterv #(= (nth % idx) least-common) bin-numbers) (+ 1 idx))))))

(deftest co2-test
  (testing "co2"
    (is (= 10 (co2 example-bin-numbers)))
    ))

(defn life-support [bin-numbers]
  (let [oxygen (oxygen bin-numbers)
        co2 (co2 bin-numbers)]
    (* oxygen co2)))

(deftest life-support-test
  (testing "life-support"
    (is (= 230 (life-support example-bin-numbers)))
    (is (= 1370737 (life-support puzzle-bin-numbers)))
    ))