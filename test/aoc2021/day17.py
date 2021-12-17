import re


def parse(text):
    groups = re.search("x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)", text).groups()
    return {"xrange": (int(groups[0]), int(groups[1])), "yrange": (int(groups[2]), int(groups[3]))}


def initial_velocities(text):
    target_area = parse(text)
    xrange = target_area["xrange"]
    yrange = target_area["yrange"]
    velocities = []
    for i in range(xrange[1] + 1):
        for j in range(yrange[0] - 1, abs(yrange[0]) + 1):
            position = {"x": 0, "y": 0}
            velocity = {"x": i, "y": j}
            max_y = float("-inf")
            while yrange[0] <= position["y"]:
                position["x"] = position["x"] + velocity["x"]
                position["y"] = position["y"] + velocity["y"]
                max_y = max(position["y"], max_y)

                if xrange[0] <= position["x"] <= xrange[1] and yrange[0] <= position["y"] <= yrange[1]:
                    velocities.append({"x": i, "y": j, "max_y": max_y})

                if velocity["x"] < 0:
                    velocity["x"] = velocity["x"] + 1
                elif velocity["x"] > 0:
                    velocity["x"] = velocity["x"] - 1

                velocity["y"] = velocity["y"] - 1

    velocities.sort(key=lambda v: v["max_y"], reverse=True)
    return velocities


def solve1(text):
    velocities = initial_velocities(text)
    return velocities[0]["max_y"]


def solve2(text):
    velocities = initial_velocities(text)
    return len(set(map(lambda v: (v["x"], v["y"]), velocities)))


print(solve1("target area: x=20..30, y=-10..-5"))
print(solve2("target area: x=20..30, y=-10..-5"))
f = open("../../resources/day17.txt")
text = f.read()
print(solve1(text))
print(solve2(text))
