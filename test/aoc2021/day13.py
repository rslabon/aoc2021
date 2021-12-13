import re

example = """
6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
""".strip()


def parse(text):
    coords, fold_instr = text.split("\n\n")
    coords = [[xy for xy in line.split(",")] for line in coords.splitlines()]
    coords = [(int(xy[0]), int(xy[1])) for xy in coords]

    fold_lines = [line.replace("fold along ", "") for line in fold_instr.splitlines()]
    fold_instrs = []
    for line in fold_lines:
        m = re.search("([y|x])=(\d+)", line)
        xy = m.group(1)
        value = int(m.group(2))
        fold_instrs.append((xy, value))

    return coords, fold_instrs


def fold_by_y(coords, y):
    r = []
    for (i, j) in coords:
        if j < y:
            r.append((i, j))
        elif j > y:
            r.append((i, 2 * y - j))

    return list(set(r))


def fold_by_x(coords, x):
    r = []
    for (i, j) in coords:
        if i < x:
            r.append((i, j))
        elif i > x:
            r.append((2 * x - i, j))

    return list(set(r))


def solve1(text):
    coords, folds = parse(text)
    fold = folds[0]
    if fold[0] == "x":
        coords = fold_by_x(coords, fold[1])
    else:
        coords = fold_by_y(coords, fold[1])

    return len(coords)


def solve2(text):
    coords, folds = parse(text)
    for fold in folds:
        if fold[0] == "x":
            coords = fold_by_x(coords, fold[1])
        else:
            coords = fold_by_y(coords, fold[1])

    width = max(map(lambda i: i[0], coords)) + 1
    height = max(map(lambda i: i[1], coords)) + 1
    m = "\n".join(["".join(["#" if (j, i) in coords else " " for j in range(width)]) for i in range(height)])
    print(m)


print(solve1(example))
f = open("../../resources/day13.txt")
print(solve1(f.read()))
f = open("../../resources/day13.txt")
print(solve2(f.read()))
