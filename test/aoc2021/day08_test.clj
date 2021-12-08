(ns aoc2021.day08_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(def puzzle-input (slurp (io/resource "day08.txt")))

(def example-input "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
                    edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
                    fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
                    fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
                    aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
                    fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
                    dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
                    bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
                    egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
                    gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce")

(defn sort-strings [xs]
  (map (fn [v] (str/join "" (sort (str/split v #"")))) xs))

(defn parse [line]
  (let [[pattern output] (str/split line #"\|")]
    [(str/split (str/trim pattern) #" ") (str/split (str/trim output) #" ")]
    ))

(defn decode [line]
  (let [[pattern output] (parse line)
        pattern (sort-strings pattern)
        output (sort-strings output)
        one (first (filter #(= (count %) 2) pattern))
        four (first (filter #(= (count %) 4) pattern))
        seven (first (filter #(= (count %) 3) pattern))
        eight (first (filter #(= (count %) 7) pattern))
        output-freq (frequencies output)
        matched (set/intersection (set output) (set [one four seven eight]))
        ]
    (reduce + (map #(get output-freq %) matched))
    ))

(deftest decode-test
  (testing "decode"
    (is (= 2 (decode "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe")))
    (is (= 4 (decode "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb")))
    ))

(defn solve-1 [input]
  (let [lines (str/split-lines input)]
    (reduce + (map decode lines))))

(deftest solve-1-test
  (testing "solve-1"
    (is (= 26 (solve-1 example-input)))
    (is (= 521 (solve-1 puzzle-input)))
    ))

(defn intersection-count [s1 s2]
  (let [s1 (str/split s1 #"")
        s2 (str/split s2 #"")]
    (count (set/intersection (set s1) (set s2)))
    ))

(defn contains-chars [s1 s2]
  (= (count s2) (intersection-count s1 s2)))

(defn find-6 [pattern eight one]
  (let [eight (str/split eight #"")
        one (str/split one #"")
        must-have-six-pattern (str/join "" (sort (vec (set/difference (set eight) (set one)))))
        six (first (filter #(and (contains-chars % must-have-six-pattern) (= 1 (- (count %) (count must-have-six-pattern)))) pattern))]
      six
    ))

(defn find-9 [pattern four]
  (first (filter #(and (contains-chars % four)) pattern)))

(defn find-0 [pattern]
  (first (filter #(= 6 (count %)) pattern)))

(defn find-3 [pattern one]
  (first (filter #(and (contains-chars % one)) pattern)))

(defn find-5 [pattern six]
  (first (filter #(= 5 (intersection-count % six)) pattern)))

(defn remove [xs ys] (vec (set/difference (set xs) (set ys))))

(defn decode2 [line]
  (let [[pattern output] (parse line)
        pattern (sort-strings pattern)
        output (sort-strings output)

        one (first (filter #(= (count %) 2) pattern))
        four (first (filter #(= (count %) 4) pattern))
        seven (first (filter #(= (count %) 3) pattern))
        eight (first (filter #(= (count %) 7) pattern))

        pattern (remove pattern [one four seven eight])
        six (find-6 pattern eight one)
        pattern (remove pattern [six])
        nine (find-9 pattern four)
        pattern (remove pattern [nine])
        zero (find-0 pattern)
        pattern (remove pattern [zero])
        three (find-3 pattern one)
        pattern (remove pattern [three])
        five (find-5 pattern six)
        pattern (remove pattern [five])
        two (first pattern)

        numbers {zero 0 one 1 two 2 three 3 four 4 five 5 six 6 seven 7 eight 8 nine 9}
        decoded (map #(get numbers %) output)
        ]
    (Long/parseLong (str/join "" decoded) 10)
    ))

(deftest decode2-test
  (testing "decode2"
    (is (= 5353 (decode2 "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")))
    (is (= 4315 (decode2 "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce")))
    ))

(defn solve-2 [input]
  (let [lines (str/split-lines input)]
    (reduce + (map decode2 lines))))

(deftest solve-2-test
  (testing "solve-2"
    (is (= 61229 (solve-2 example-input)))
    (is (= 1016804 (solve-2 puzzle-input)))
    ))