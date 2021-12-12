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

    def paths(self, max_small, node, path, all_paths):
        if not node.value.isupper() and node in path:
            max_small -= 1
            if max_small == 0 or node.value == "start":
                return

        path.append(node)
        if node.value == "end":
            all_paths.append(path.copy())
            return
        else:
            for n in node.adj:
                self.paths(max_small, n, path.copy(), all_paths)


def parse(text):
    g = Graph()
    for line in text.splitlines():
        a, b = line.split("-")
        g.add_edge(a, b)

    return g


def solve1(text):
    g = parse(text)
    paths = []
    g.paths(1, g.get_node("start"), [], paths)
    return len(paths)


def solve2(text):
    g = parse(text)
    paths = []
    g.paths(2, g.get_node("start"), [], paths)
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
