import math

example = """
..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###
""".strip()


def parse(text):
    algorithm, image = text.split("\n\n")
    image = [[c for c in line] for line in image.splitlines()]
    return algorithm, image


def binary_number(boundary, image, i, j):
    square = [(i - 1, j - 1), (i - 1, j), (i - 1, j + 1),
              (i, j - 1), (i, j), (i, j + 1),
              (i + 1, j - 1), (i + 1, j), (i + 1, j + 1)]

    values = []
    for (i, j) in square:
        v = boundary
        if 0 <= i < len(image) and 0 <= j < len(image[0]):
            v = image[i][j]
        values.append(v)

    p = 8
    number = 0
    for v in values:
        if v == "#":
            number += math.pow(2, p)
        p -= 1
    return int(number)


def step(boundary, algorithm, image):
    output = []
    for i in range(-2, len(image) + 2):
        row = []
        for j in range(-2, len(image[0]) + 2):
            n = binary_number(algorithm[boundary], image, i, j)
            a = algorithm[n]
            row.append(a)

        output.append(row)

    if algorithm[boundary] == "#":
        boundary = 511
    else:
        boundary = 0

    return output, boundary


def show(image):
    print("\n".join(["".join([j for j in i]) for i in image]) + "\n\n")


def number_of_lit(image):
    n = 0
    for row in image:
        for cell in row:
            if cell == "#":
                n += 1
    return n


def solve(text, n):
    algorithm, image = parse(text)
    if algorithm[0] == "#":
        boundary = 511
    else:
        boundary = 0
    for _ in range(n):
        image, boundary = step(boundary, algorithm, image)

    return number_of_lit(image)


def solve1(text):
    return solve(text, 2)


def solve2(text):
    return solve(text, 50)


f = open("../../resources/day20.txt")
puzzle = f.read()

print(solve1(example))
print(solve1(puzzle))

print(solve2(example))
print(solve2(puzzle))
