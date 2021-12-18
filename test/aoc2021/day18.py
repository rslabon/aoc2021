import math


class Node:
    def __init__(self, value=None, left=None, right=None):
        self.parent = None
        self.value = None
        self.left = None
        self.right = None

        if value is not None:
            self.value = value
        else:
            self.left = left
            self.left.parent = self
            self.right = right
            self.right.parent = self

    @staticmethod
    def pair(left, right):
        return Node(None, left, right)

    @staticmethod
    def value(value):
        return Node(value, None, None)

    def is_regular(self):
        return self.value is not None

    def __repr__(self):
        if self.is_regular():
            if self.value >= 10:
                return str(self.value) + "*"
            else:
                return str(self.value)
        else:
            if self.depth() == 4:
                return "[%s, %s]*" % (str(self.left), str(self.right))
            else:
                return "[%s, %s]" % (str(self.left), str(self.right))

    def __str__(self):
        return self.__repr__()

    def depth(self):
        p = self.parent
        i = 0
        while p:
            i += 1
            p = p.parent

        return i

    def add(self, node):
        return Node.pair(self, node)

    def split(self):
        if self.is_regular() and self.value >= 10:
            v = self.value
            self.value = None
            self.left = Node.value(math.floor(v / 2))
            self.left.parent = self
            self.right = Node.value(math.ceil(v / 2))
            self.right.parent = self
            return True

        if not self.is_regular():
            return self.left.split() or self.right.split()
        return False

    def explode(self):
        if not self.is_regular() and self.depth() == 4:
            self.add_left()
            self.add_right()
            self.left = None
            self.right = None
            self.value = 0
            return True

        if not self.is_regular():
            return self.left.explode() or self.right.explode()
        return False

    def reduce(self):
        while self.explode() or self.split():
            pass

    def add_right(self):
        current = self
        parent = self.parent
        while parent and parent.right == current:
            current = parent
            parent = parent.parent

        if not parent:
            return

        current = parent.right
        while current and not current.is_regular():
            current = current.left

        if current:
            current.value += self.right.value

    def add_left(self):
        current = self
        parent = self.parent
        while parent and parent.left == current:
            current = parent
            parent = parent.parent

        if not parent:
            return

        current = parent.left
        while current and not current.is_regular():
            current = current.right

        if current:
            current.value += self.left.value

    def magnitude(self):
        if self.is_regular():
            return self.value
        return 3 * self.left.magnitude() + 2 * self.right.magnitude()


def nodify(numbers):
    if isinstance(numbers, list):
        return Node.pair(nodify(numbers[0]), nodify(numbers[1]))
    else:
        return Node.value(numbers)


def parse(line):
    return nodify(eval(line))


def solve1(text):
    pairs = [parse(line) for line in text.splitlines()]
    root = pairs[0]
    for node in pairs[1::]:
        root = root.add(node)
        root.reduce()

    return root.magnitude()


def solve2(text):
    combination = [(line1, line2) for line1 in text.splitlines() for line2 in text.splitlines() if line1 != line2]
    max_sum = float("-inf")
    for (line1, line2) in combination:
        p1 = parse(line1)
        p2 = parse(line2)
        p = p1.add(p2)
        p.reduce()
        max_sum = max(max_sum, p.magnitude())

    return max_sum


example = """
[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
[7,[5,[[3,8],[1,4]]]]
[[2,[2,2]],[8,[8,1]]]
[2,9]
[1,[[[9,3],9],[[9,0],[0,7]]]]
[[[5,[7,4]],7],1]
[[[[4,2],2],6],[8,7]]
""".strip()

f = open("../../resources/day18.txt")
text = f.read()
print(solve1(example))
print(solve1(text))

example = """
[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
""".strip()

print(solve2(example))
print(solve2(text))
