from collections import Counter

example = """
NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C
""".strip()


def parse(text):
    template, insertions = text.split("\n\n")
    insertions = insertions.splitlines()
    insertions = [i.replace(" ", "").split("->") for i in insertions]
    return template, insertions


def freq(template):
    result = {}
    while template:
        a = template[0:2]
        if len(a) == 2:
            result[a] = 1 + result.get(a, 0)
        template = template[1::]
    return result


def solve(steps, text):
    template, insertions = parse(text)
    rules = {}
    transition = {}
    for a, b in insertions:
        rules[a] = (a[0] + b, b + a[1])
        transition[a] = b

    step_freq = freq(template)
    count_letters = Counter([c for c in template])

    for _ in range(steps):
        next_step_freq = {}
        for step_rule in step_freq.keys():
            occurrences = step_freq[step_rule]
            r1, r2 = rules[step_rule]
            count_letters[transition[step_rule]] = occurrences + count_letters.get(transition[step_rule], 0)
            next_step_freq[r1] = occurrences + next_step_freq.get(r1, 0)
            next_step_freq[r2] = occurrences + next_step_freq.get(r2, 0)
        step_freq = next_step_freq

    return max(count_letters.values()) - min(count_letters.values())


def solve1(text):
    return solve(10, text)


def solve2(text):
    return solve(40, text)


print(solve1(example))
f = open("../../resources/day14.txt")
print(solve1(f.read()))

print(solve2(example))
f = open("../../resources/day14.txt")
print(solve2(f.read()))
