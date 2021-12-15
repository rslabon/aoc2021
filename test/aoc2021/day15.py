import heapq

example = """
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
""".strip()


class Node:

    def __init__(self, value):
        self.value = value
        self.adj = set([])

    def __repr__(self):
        return str(self.value)

    def __eq__(self, other):
        return self.value == other.value

    def __hash__(self):
        return hash(self.value)

    def __lt__(self, other):
        return self.value[2] < other.value[2]


class Graph:

    def __init__(self):
        self.nodes = {}
        self.distTo = {}
        self.edgeTo = {}

    def get_node(self, val):
        return self.nodes.get(val)

    def add_edge(self, a, b):
        n1 = self.nodes.get(a, Node(a))
        n2 = self.nodes.get(b, Node(b))
        self.nodes[a] = n1
        self.nodes[b] = n2

        n1.adj.add(n2)

    def relax(self, node, pq):
        for other_node in node.adj:
            if self.distTo[other_node] > self.distTo[node] + node.value[2]:
                old = self.distTo[other_node]
                self.distTo[other_node] = self.distTo[node] + node.value[2]
                self.edgeTo[other_node] = node
                if (old, other_node) in pq:
                    pq.remove((old, other_node))

                heapq.heappush(pq, (self.distTo[other_node], other_node))

    def path_to(self, node):
        e = self.edgeTo[node]
        path = [node, e]
        while e in self.edgeTo:
            e = self.edgeTo[e]
            path.append(e)
        return path

    def compute_paths(self, source_node):
        self.distTo = {}
        self.edgeTo = {}
        pq = []
        for node in self.nodes.values():
            self.distTo[node] = float("inf")

        self.distTo[source_node] = 0
        pq.append((0, source_node))
        heapq.heapify(pq)

        while len(pq) > 0:
            _, node = heapq.heappop(pq)
            self.relax(node, pq)


def parse(text):
    graph = Graph()
    grid = [[int(v) for v in line] for line in text.splitlines()]
    height = len(grid)
    width = len(grid[0])
    indices = [(i, j) for i in range(len(grid)) for j in range(width)]

    for i, j in indices:
        v = grid[i][j]
        adj = [(i - 1, j), (i + 1, j), (i, j - 1), (i, j + 1)]
        for ni, nj in adj:
            if 0 <= ni < height and 0 <= nj < width:
                graph.add_edge((i, j, v), (ni, nj, grid[ni][nj]))

    return graph, grid


def increase_grid(n, original_grid):
    height = len(original_grid)
    width = len(original_grid[0])
    indices = [(i, j) for i in range(height * n) for j in range(width * n)]

    grid = [[-1 for _ in range(width * n)] for _ in range(height * n)]
    for i, j in indices:
        l = i // height + j // width
        v = original_grid[i % height][j % width] + l
        if v > 9:
            v = (v % 10) + 1
        grid[i][j] = v
    return grid


def make_graph(grid):
    graph = Graph()
    height = len(grid)
    width = len(grid[0])
    indices = [(i, j) for i in range(height) for j in range(width)]

    for i, j in indices:
        adj = [(i - 1, j), (i + 1, j), (i, j - 1), (i, j + 1)]
        v = grid[i][j]
        for ni, nj in adj:
            if 0 <= ni < height and 0 <= nj < width:
                nv = grid[ni][nj]
                graph.add_edge((i, j, v), (ni, nj, nv))

    return graph


def solve(graph, grid):
    height = len(grid)
    width = len(grid[0])

    entry_value = grid[0][0]
    top_left_node = graph.get_node((0, 0, entry_value))
    bottom_right_node = graph.get_node((height - 1, width - 1, grid[height - 1][width - 1]))

    graph.compute_paths(top_left_node)
    path = graph.path_to(bottom_right_node)

    s = 0
    for node in path:
        s += node.value[2]
    s -= entry_value

    return s


def solve1(text):
    grid = [[int(v) for v in line] for line in text.splitlines()]
    graph = make_graph(grid)
    return solve(graph, grid)


def solve2(text):
    original_grid = [[int(v) for v in line] for line in text.splitlines()]
    grid = increase_grid(5, original_grid)
    graph = make_graph(grid)
    return solve(graph, grid)


print(solve1(example))
f = open("../../resources/day15.txt")
print(solve1(f.read()))

print(solve2(example))
f = open("../../resources/day15.txt")
print(solve2(f.read()))
