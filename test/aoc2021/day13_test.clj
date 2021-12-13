(ns aoc2021.day13_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def example-input "6,10\n0,14\n9,10\n0,3\n10,4\n4,11\n6,0\n6,12\n4,1\n0,13\n10,12\n3,4\n3,0\n8,4\n1,10\n2,14\n8,10\n9,0\n\nfold along y=7\nfold along x=5")
(def puzzle-input (slurp (io/resource "day13.txt")))

(defn parse-coord-line [line]
  (let [[x y] (str/split line #",")]
    [(read-string x) (read-string y)]
    ))

(defn parse-fold-line [line]
  (let [[_ value] (str/split line #"=")]
    [(if (str/includes? line "x") :x :y) (read-string value)]
    ))

(defn parse [input]
  (let [[coords fold-instrs] (str/split input #"\n\n")
        coords (map parse-coord-line (str/split-lines coords))
        fold-instrs (map parse-fold-line (str/split-lines fold-instrs))]
    [coords fold-instrs]
    ))

(defn fold [coords, [pos val]]
  (let [coords (filter (fn [[i, j]]
                         (condp = pos
                           :x (not= i val)
                           :y (not= j val)))
                       coords)
        coords (map (fn [[i, j]]
                      (condp = pos
                        :x [(if (< i val) i (- (* 2 val) i)), j]
                        :y [i, (if (< j val) j (- (* 2 val) j))]))
                    coords)]
    (set coords)))

(defn solve1 [input]
  (let [[coords fold-instrs] (parse input)
        first-fold (first fold-instrs)
        coords (fold coords first-fold)
        _ (println coords)]
    (count coords)
    ))

(defn solve2 [input]
  (let [[coords fold-instrs] (parse input)
        coords (set (reduce #(fold %1 %2) coords fold-instrs))
        width (+ 1 (apply max (map #(nth % 0) coords)))
        height (+ 1 (apply max (map #(nth % 1) coords)))
        grid (str/join "\n " (for [j (range height)]
                               (str/join (for [i (range width)]
                                           (if (contains? coords [i, j]) "#" " ")))))]
    (println grid)
    ))

(deftest solve1-test
  (testing "solve1"
    (is (= 17 (solve1 example-input)))
    (is (= 695 (solve1 puzzle-input)))
    ))

(defn -main []
  (solve2 puzzle-input))