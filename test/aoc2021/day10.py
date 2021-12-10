import math


def find_illegal1(chars):
    stack = []
    score = {")": 3, "]": 57, "}": 1197, ">": 25137}
    open_close = {"(": ")", "{": "}", "<": ">", "[": "]"}
    for c in chars:
        if c in "([{<":
            stack.append(c)
        else:
            o = stack.pop()
            if open_close.get(o) != c:
                return score.get(c)

    return 0


def find_illegal2(chars):
    stack = []
    score = {")": 1, "]": 2, "}": 3, ">": 4}
    open_close = {"(": ")", "{": "}", "<": ">", "[": "]"}
    for c in chars:
        if c in "([{<":
            stack.append(c)
        else:
            o = stack.pop()
            if open_close.get(o) != c:
                return 0

    result = 0
    for c in reversed(stack):
        result *= 5
        o = open_close.get(c)
        result += score.get(o)
    return result


example = """
[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]
""".strip()


def solve1(input):
    lines = input.splitlines()
    result = 0
    for line in lines:
        i = find_illegal1(line)
        result += i
    return result


def solve2(input):
    lines = input.splitlines()
    scores = []
    for line in lines:
        i = find_illegal2(line)
        if i > 0:
            scores.append(i)

    scores = sorted(scores)
    return scores[math.floor(len(scores) / 2)]


print(solve1(example))
f = open("../../resources/day10.txt")
print(solve1(f.read()))

f = open("../../resources/day10.txt")
print(solve2(example))
print(solve2(f.read()))
