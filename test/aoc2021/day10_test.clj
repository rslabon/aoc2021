(ns aoc2021.day10_test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def example-input "[({(<(())[]>[[{[]{<()<>>\n[(()[<>])]({[<{<<[]>>(\n{([(<{}[<>[]}>{[]{[(<()>\n(((({<>}<{<{<>}{[]{[]{}\n[[<[([]))<([[{}[[()]]]\n[{[{({}]{}}([{[{{{}}([]\n{<[[]]>}<{[{[{[]{()[[[]\n[<(<(<(<{}))><([]([]()\n<{([([[(<>()){}]>(<<{{\n<{([{{}}[<[[[<>{}]]]>[]]")
(def puzzle-input (slurp (io/resource "day10.txt")))

(defn open? [char]
  (str/includes? "([{<" char))

(defn close-for [open-char]
  (let [open-close {"(" ")" "{" "}" "[" "]" "<" ">"}] (get open-close open-char)))

(defn illegal-close? [open-char close-char]
  (not= (close-for open-char) close-char))

(defn find-first-illegal
  ([chunk] (let [chars (str/split chunk #"")]
             (loop [chars chars
                    stack '()]
               (cond
                 (empty? chars) nil
                 (open? (first chars)) (recur (rest chars) (conj stack (first chars)))
                 (illegal-close? (first stack) (first chars)) (first chars)
                 :else (recur (rest chars) (rest stack))
                 )))))

(defn find-all-missing
  ([chunk] (let [chars (str/split chunk #"")]
             (loop [chars chars
                    stack '()]
               (cond
                 (empty? chars) (map close-for stack)
                 (open? (first chars)) (recur (rest chars) (conj stack (first chars)))
                 (illegal-close? (first stack) (first chars)) '()
                 :else (recur (rest chars) (rest stack))
                 )))))

(defn solve1 [input]
  (let [lines (str/split-lines input)
        illegals (filter not-empty (map find-first-illegal lines))
        scores {")" 3 "]" 57 "}" 1197 ">" 25137}
        score (reduce + (map #(get scores %) illegals))]
    score
    ))

(defn points [missing-brackets]
  (let [scores {")" 1 "]" 2 "}" 3 ">" 4}]
    (reduce (fn [acc, b] (+ (* acc 5) (get scores b))) 0 missing-brackets)))

(defn solve2 [input]
  (let [lines (str/split-lines input)
        missing (filter not-empty (map find-all-missing lines))
        points (map points missing)
        points (sort points)
        middle (nth points (Math/floor (/ (count points) 2)))]
    middle
    ))

(deftest complete-test
  (testing "complete"
    (is (= nil (find-first-illegal "()")))
    (is (= "]" (find-first-illegal "(]")))
    (is (= nil (find-first-illegal "(())")))
    (is (= "}" (find-first-illegal "{([(<{}[<>[]}>{[]{[(<()>")))
    (is (= ">" (find-first-illegal "<{([([[(<>()){}]>(<<{{")))
    ))

(deftest solve1-test
  (testing "solve1"
    (is (= 26397 (solve1 example-input)))
    (is (= 388713 (solve1 puzzle-input)))
    ))

(deftest solve2-test
  (testing "solve2"
    (is (= 288957 (solve2 example-input)))
    (is (= 3539961434 (solve2 puzzle-input)))
    ))