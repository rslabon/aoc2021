from math import prod

example = "2199943210\n3987894921\n9856789892\n8767896789\n9899965678"


def parse_matrix(text):
    lines = text.splitlines()
    return [[int(c) for c in line] for line in lines]


def height(matrix):
    return len(matrix)


def width(matrix):
    return len(matrix[0])


def is_low_point(matrix, x, y):
    adj = [(x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)]
    adj = filter(lambda xy: 0 <= xy[0] < height(matrix) and 0 <= xy[1] < width(matrix), adj)
    current = matrix[x][y]
    adj = map(lambda xy: matrix[xy[0]][xy[1]], adj)
    adj = map(lambda val: current < val, adj)
    return all(adj)


def find_low_points(matrix):
    low_points = []
    for i in range(height(matrix)):
        for j in range(width(matrix)):
            if is_low_point(matrix, i, j):
                low_points.append(matrix[i][j])
    return low_points


def solve1(matrix):
    return sum(map(lambda i: i + 1, find_low_points(matrix)))


def basin(matrix, prev, x, y, visited):
    if (x, y) not in visited and 0 <= x < height(matrix) and 0 <= y < width(matrix):
        current = matrix[x][y]
        if current == 9:
            visited.append((x, y))
            return 0
        if prev is None or current > prev:
            visited.append((x, y))
            b = 1
            b += basin(matrix, current, x - 1, y, visited)
            b += basin(matrix, current, x + 1, y, visited)
            b += basin(matrix, current, x, y - 1, visited)
            b += basin(matrix, current, x, y + 1, visited)
            return b

    return 0


def solve2(matrix):
    b = []
    for i in range(height(matrix)):
        for j in range(width(matrix)):
            if is_low_point(matrix, i, j):
                b.append(basin(matrix, None, i, j, []))

    top_basins = sorted(b, reverse=True)[0:3]
    return prod(top_basins)


example_matrix = parse_matrix(example)
print(solve1(example_matrix))
print(solve2(example_matrix))

f = open("../../resources/day09.txt", "r")
puzzle_matrix = parse_matrix(f.read())
print(solve1(puzzle_matrix))
print(solve2(puzzle_matrix))
