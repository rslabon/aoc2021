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


def step(template, dic):
    result = template[0:1]
    while template:
        a = template[0:2]
        if len(a) == 2:
            result += dic[a] + a[1]
        template = template[1::]
    return result


def solve1(text):
    template, insertions = parse(text)
    dic = {}
    for a, b in insertions:
        dic[a] = b

    for _ in range(10):
        template = step(template, dic)

    counter = Counter([c for c in template])
    result = max(counter.values()) - min(counter.values())
    return result


def freq(template):
    result = {}
    while template:
        a = template[0:2]
        if len(a) == 2:
            result[a] = 1 + result.get(a, 0)
        template = template[1::]
    return result


def solve2(text):
    template, insertions = parse(text)
    rules = {}
    transition = {}
    for a, b in insertions:
        rules[a] = (a[0] + b, b + a[1])
        transition[a] = b

    f = freq(template)
    c = Counter([c for c in template])

    for o in range(40):
        ftmp = {}
        for k in f.keys():
            x = f[k]
            r1, r2 = rules[k]
            c[transition[k]] = x + c.get(transition[k], 0)
            ftmp[r1] = x + ftmp.get(r1, 0)
            ftmp[r2] = x + ftmp.get(r2, 0)
        f = ftmp

    result = max(c.values()) - min(c.values())
    return result


print(solve1(example))
f = open("../../resources/day14.txt")
print(solve1(f.read()))

print(solve2(example))
f = open("../../resources/day14.txt")
print(solve2(f.read()))
