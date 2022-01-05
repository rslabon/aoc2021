import re


def volume(r):
    x, y, z = r
    return (abs(abs(x[0]) - abs(x[1])) + 1) * (abs(abs(y[0]) - abs(y[1])) + 1) * (abs(abs(z[0]) - abs(z[1])) + 1)


def __inter__(r1, r2):
    xr1, yr1, zr1 = r1
    xr2, yr2, zr2 = r2

    if xr1[0] <= xr2[0] and xr1[1] <= xr2[1] and \
            yr1[0] <= yr2[0] and yr1[1] <= yr2[1] and \
            zr1[0] <= zr2[0] and zr1[1] <= zr2[1]:
        x = (max(xr1[0], xr2[0]), min(xr1[1], xr2[1]))
        y = (max(yr1[0], yr2[0]), min(yr1[1], yr2[1]))
        z = (max(zr1[0], zr2[0]), min(zr1[1], zr2[1]))
        return x, y, z


def intersection(r1, r2):
    return __inter__(r1, r2) or __inter__(r2, r1)


def solve(text):
    ranges = []
    for line in text.splitlines():
        m = re.search("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)", line).groups()
        r = ((int(m[1]), int(m[2])), (int(m[3]), int(m[4])), (int(m[5]), int(m[6])))
        ranges.append((m[0], r))

    volumes = [0] * len(ranges)
    volumes[0] = volume(ranges[1][1])
    for i in range(len(ranges)):
        for j in range(i):
            if j < i:
                ir = intersection(ranges[i][1], ranges[j][1]) or ranges[i][1]
                if ir:
                    if ranges[i][0] == "on":
                        volumes[i] += volume(ranges[i][1]) - volume(ir)
                    else:
                        volumes[i] = max(volumes[i] - volume(ir), 0)
                else:
                    if ranges[i][0] == "on":
                        volumes[i] += volume(ranges[i][1])
                    else:
                        volumes[i] = 0

    print(volumes)
    print(sum(volumes))
    # v = volume(ranges[1][1])
    # x = volume(intersection(ranges[0][1], ranges[1][1]))
    # print(v)
    # print(x)


example = """
on x=-20..26,y=-36..17,z=-47..7
on x=-20..33,y=-21..23,z=-26..28
on x=-22..28,y=-29..23,z=-38..16
on x=-46..7,y=-6..46,z=-50..-1
on x=-49..1,y=-3..46,z=-24..28
on x=2..47,y=-22..22,z=-23..27
on x=-27..23,y=-28..26,z=-21..29
on x=-39..5,y=-6..47,z=-3..44
on x=-30..21,y=-8..43,z=-13..34
on x=-22..26,y=-27..20,z=-29..19
off x=-48..-32,y=26..41,z=-47..-37
on x=-12..35,y=6..50,z=-50..-2
off x=-48..-32,y=-32..-16,z=-15..-5
on x=-18..26,y=-33..15,z=-7..46
off x=-40..-22,y=-38..-28,z=23..41
on x=-16..35,y=-41..10,z=-47..6
off x=-32..-23,y=11..30,z=-14..3
on x=-49..-5,y=-3..45,z=-29..18
off x=18..30,y=-20..-8,z=-3..13
on x=-41..9,y=-7..43,z=-33..15
""".strip()

solve(example)
