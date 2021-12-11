def flash(grid, i, j, flashed):
    if (i, j) not in flashed and 0 <= i < len(grid) and 0 <= j < len(grid[0]):
        if grid[i][j] == 9:
            grid[i][j] = 0
            flashed.append((i, j))
            r = 1
            r += flash(grid, i - 1, j, flashed)
            r += flash(grid, i + 1, j, flashed)
            r += flash(grid, i, j - 1, flashed)
            r += flash(grid, i, j + 1, flashed)
            r += flash(grid, i - 1, j - 1, flashed)
            r += flash(grid, i - 1, j + 1, flashed)
            r += flash(grid, i + 1, j - 1, flashed)
            r += flash(grid, i + 1, j + 1, flashed)
            return r
        else:
            grid[i][j] += 1
            return 0
    return 0


def step(grid):
    num_of_flashes = 0
    flashed = []
    for i in range(len(grid)):
        for j in range(len(grid[0])):
            num_of_flashes += flash(grid, i, j, flashed)
    return num_of_flashes


example = """
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526
""".strip()


def parse(text):
    grid = []
    for line in text.splitlines():
        grid.append([int(c) for c in line])
    return grid


def solve1(text):
    grid = parse(text)
    num_of_flashes = 0
    for i in range(100):
        num_of_flashes += step(grid)

    return num_of_flashes


def all_flashed(grid):
    for i in range(len(grid)):
        for j in range(len(grid[0])):
            if grid[i][j] != 0:
                return False
    return True


def solve2(text):
    grid = parse(text)
    s = 0
    while not all_flashed(grid):
        step(grid)
        s += 1

    return s


print(solve1(example))
f = open("../../resources/day11.txt", "r")
print(solve1(f.read()))
print(solve2(example))
f = open("../../resources/day11.txt", "r")
print(solve2(f.read()))
