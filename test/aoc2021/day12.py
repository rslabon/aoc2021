from collections import Counter


class Node:
    value = ""
    adj = []

    def __init__(self, value):
        self.value = value
        self.adj = []

    def __repr__(self):
        return self.value


class Graph:
    nodes = {}

    def __init__(self):
        self.nodes = {}

    def get_node(self, val):
        return self.nodes.get(val)

    def add_edge(self, a, b):
        n1 = self.nodes.get(a, Node(a))
        n2 = self.nodes.get(b, Node(b))
        self.nodes[a] = n1
        self.nodes[b] = n2

        n1.adj.append(n2)
        n2.adj.append(n1)

    @staticmethod
    def visit_small_only_once(node, path):
        return not node.value.isupper() and node in path

    @staticmethod
    def visit_small_only_once(node, path):
        return not node.value.isupper() and node in path

    @staticmethod
    def visit_single_small_twice(node, path):
        if node.value == "start" and node in path:
            return True

        visited_small_caves = list(filter(lambda n: n.value.islower() and n.value not in ["start", "end"], path))
        if len(visited_small_caves) > 0:
            counter = Counter(visited_small_caves)
            allowed_max = 0
            if max(counter.values()) < 2:
                allowed_max += 1
            if counter[node] > allowed_max:
                return True

        return False

    def paths(self, visited_fn, node, path, all_paths):
        if visited_fn(node, path):
            return

        path.append(node)
        if node.value == "end":
            all_paths.append(path.copy())
            return
        else:
            for n in node.adj:
                self.paths(visited_fn, n, path.copy(), all_paths)


def parse(text):
    g = Graph()
    for line in text.splitlines():
        a, b = line.split("-")
        g.add_edge(a, b)

    return g


def solve1(text):
    g = parse(text)
    paths = []
    g.paths(Graph.visit_small_only_once, g.get_node("start"), [], paths)
    return len(paths)


def solve2(text):
    g = parse(text)
    paths = []
    g.paths(Graph.visit_single_small_twice, g.get_node("start"), [], paths)
    return len(paths)


example = """
start-A
start-b
A-c
A-b
b-d
A-end
b-end
""".strip()

print(solve1(example))
f = open("../../resources/day12.txt")
print(solve1(f.read()))

print(solve2(example))
f = open("../../resources/day12.txt")
print(solve2(f.read()))
