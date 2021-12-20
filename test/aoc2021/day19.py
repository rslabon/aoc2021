import math
from collections import Counter

example = """
--- scanner 0 ---
404,-588,-901
528,-643,409
-838,591,734
390,-675,-793
-537,-823,-458
-485,-357,347
-345,-311,381
-661,-816,-575
-876,649,763
-618,-824,-621
553,345,-567
474,580,667
-447,-329,318
-584,868,-557
544,-627,-890
564,392,-477
455,729,728
-892,524,684
-689,845,-530
423,-701,434
7,-33,-71
630,319,-379
443,580,662
-789,900,-551
459,-707,401

--- scanner 1 ---
686,422,578
605,423,415
515,917,-361
-336,658,858
95,138,22
-476,619,847
-340,-569,-846
567,-361,727
-460,603,-452
669,-402,600
729,430,532
-500,-761,534
-322,571,750
-466,-666,-811
-429,-592,574
-355,545,-477
703,-491,-529
-328,-685,520
413,935,-424
-391,539,-444
586,-435,557
-364,-763,-893
807,-499,-711
755,-354,-619
553,889,-390

--- scanner 2 ---
649,640,665
682,-795,504
-784,533,-524
-644,584,-595
-588,-843,648
-30,6,44
-674,560,763
500,723,-460
609,671,-379
-555,-800,653
-675,-892,-343
697,-426,-610
578,704,681
493,664,-388
-671,-858,530
-667,343,800
571,-461,-707
-138,-166,112
-889,563,-600
646,-828,498
640,759,510
-630,509,768
-681,-892,-333
673,-379,-804
-742,-814,-386
577,-820,562

--- scanner 3 ---
-589,542,597
605,-692,669
-500,565,-823
-660,373,557
-458,-679,-417
-488,449,543
-626,468,-788
338,-750,-386
528,-832,-391
562,-778,733
-938,-730,414
543,643,-506
-524,371,-870
407,773,750
-104,29,83
378,-903,-323
-778,-728,485
426,699,580
-438,-605,-362
-469,-447,-387
509,732,623
647,635,-688
-868,-804,481
614,-800,639
595,780,-596

--- scanner 4 ---
727,592,562
-293,-554,779
441,611,-461
-714,465,-776
-743,427,-804
-660,-479,-426
832,-632,460
927,-485,-438
408,393,-506
466,436,-512
110,16,151
-258,-428,682
-393,719,612
-211,-452,876
808,-476,-593
-575,615,604
-485,667,467
-680,325,-822
-627,-443,-432
872,-547,-609
833,512,582
807,604,487
839,-516,451
891,-625,532
-652,-548,-490
30,-46,-14
""".strip()


def distance(p1, p2):
    x1, y1, z1 = p1
    x2, y2, z2 = p2
    x = x2 - x1
    y = y2 - y1
    z = z2 - z1
    return math.sqrt(x * x + y * y + z * z)


def distances(points):
    result = {}
    for p1 in points:
        for p2 in points:
            if p1 != p2:
                d = distance(p1, p2)
                result[d] = {"p1": p1, "p2": p2}
    return result


def find_beacons(points_a, points_b):
    distance_a = distances(points_a)
    distance_b = distances(points_b)

    the_same_distances = set.intersection(set(distance_a.keys()), set(distance_b.keys()))

    beacons_a = set([])
    beacons_b = set([])

    translation = {}
    for d in the_same_distances:
        dax = distance_a[d]
        dbx = distance_b[d]

        t = translation.get(dax["p1"], [])
        t.append(dbx["p1"])
        t.append(dbx["p2"])
        translation[dax["p1"]] = t

        t = translation.get(dax["p2"], [])
        t.append(dbx["p1"])
        t.append(dbx["p2"])
        translation[dax["p2"]] = t

        beacons_a.add(dax["p1"])
        beacons_a.add(dax["p2"])
        beacons_b.add(dbx["p1"])
        beacons_b.add(dbx["p2"])

    if len(beacons_a) == len(beacons_b) == 12:
        result = []
        for beacon_a in translation:
            c = Counter(translation[beacon_a])
            beacon_b = max(c.items(), key=lambda i: i[1])[0]
            result.append((beacon_a, beacon_b))
        return result
    return []


def parse(text):
    i = -1
    scanner_report = {}
    for line in text.splitlines():
        if "scanner" in line:
            i += 1
            scanner_report[i] = []
        elif line:
            scanner_report[i].append(tuple([int(c.strip()) for c in line.split(",")]))
    return scanner_report


def diff(a, b, i):
    return [+a[i] - b[i], -a[i] + b[i], +a[i] - b[i], +a[i] + b[i]]


def transform(p1, t):
    x, y, z = p1
    return [[x, z, -y], [-z, x, -y], [-x, -z, -y],
            [z, -x, -y], [z, -y, x], [y, z, x],
            [-z, y, x], [-y, -z, x], [-y, x, z],
            [-x, -y, z], [y, -x, z], [x, y, z],
            [-z, -x, y], [x, -z, y], [z, x, y],
            [-x, z, y], [-x, y, -z], [-y, -x, -z],
            [x, -y, -z], [y, x, -z], [y, -z, -x],
            [z, y, -x], [-y, z, -x], [-z, -y, -x]][t]


def solve1(text):
    scanner_report = parse(text)
    scanner_report2 = parse(text)
    i = 0
    n = len(scanner_report)
    removed = {}
    while i < n:
        j = i + 1
        while j < n:
            print("*" * 100)
            print("Scanner %d -> %d" % (i, j))
            beacons = find_beacons(scanner_report[i], scanner_report[j])
            print(beacons)
            if beacons:
                for (a, b) in beacons:
                    if a in scanner_report2[i]:
                        s = removed.get(i, set([]))
                        s.add(a)
                        removed[i] = s
                        scanner_report2[i].remove(a)
                    if b in scanner_report2[j]:
                        s = removed.get(j, set([]))
                        s.add(b)
                        removed[j] = s
                        scanner_report2[j].remove(b)
            j += 1
        i += 1

    print(scanner_report2)


print(solve1(example))
