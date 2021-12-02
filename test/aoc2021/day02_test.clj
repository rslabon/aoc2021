(ns aoc2021.day02_test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def puzzle-input (slurp (io/resource "day02.txt")))

(defn parse-line
  [line] (let [[_ command units] (re-find #"(forward|up|down) (\d+)" line)]
           [(keyword command) (read-string units)]))

(def puzzle-actions (map parse-line (str/split-lines puzzle-input)))

(defn action
  ([command units] (action command units {:horizontal 0 :depth 0}))
  ([command units state] (condp = command
                           :forward (update state :horizontal + units)
                           :up (update state :depth - units)
                           :down (update state :depth + units))))

(deftest action-test
  (testing "action"
    (is (= {:horizontal 1 :depth 0} (action :forward 1)))
    (is (= {:horizontal 0 :depth -1} (action :up 1)))
    (is (= {:horizontal 0 :depth 1} (action :down 1)))))

(defn solve-1
  [actions] (loop [state {:horizontal 0 :depth 0}
                   actions actions]
              (if (empty? actions)
                (* (:horizontal state) (:depth state))
                (let [[command units] (first actions)]
                  (recur (action command units state) (rest actions))))))

(deftest solve-1-test
  (testing "solve 1"
    (is (= 150 (solve-1 [[:forward 5]
                         [:down 5]
                         [:forward 8]
                         [:up 3]
                         [:down 8]
                         [:forward 2]])))
    (is (= 1499229 (solve-1 puzzle-actions)))))

(defn action2
  ([command units] (action2 command units {:horizontal 0 :depth 0 :aim 0}))
  ([command units state] (condp = command
                           :forward (-> state
                                        (update :horizontal + units)
                                        (update :depth + (* (:aim state) units)))
                           :up (update state :aim - units)
                           :down (update state :aim + units))))

(deftest action2-test
  (testing "action2"
    (is (= {:horizontal 3 :depth 6 :aim 2} (action2 :forward 3 {:horizontal 0 :depth 0 :aim 2})))
    (is (= {:horizontal 0 :depth 0 :aim -1} (action2 :up 1)))
    (is (= {:horizontal 0 :depth 0 :aim 1} (action2 :down 1)))))

(defn solve-2
  [actions] (loop [state {:horizontal 0 :depth 0 :aim 0}
                   actions actions]
              (if (empty? actions)
                (* (:horizontal state) (:depth state))
                (let [[command units] (first actions)]
                  (recur (action2 command units state) (rest actions))))))

(deftest solve-2-test
  (testing "solve 2"
    (is (= 900 (solve-2 [[:forward 5]
                         [:down 5]
                         [:forward 8]
                         [:up 3]
                         [:down 8]
                         [:forward 2]])))
    (is (= 1340836560 (solve-2 puzzle-actions)))))

