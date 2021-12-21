import re
from functools import cache

example = """
Player 1 starting position: 4
Player 2 starting position: 8
""".strip()


def cyclic(v, n):
    return (v - 1) % n + 1


def next_dice(dice):
    return cyclic(3 * dice + 3, 100)


def next_position(player):
    return cyclic(player, 10)


def roll(p1, p2):
    dice = 1
    p1_score = 0
    p2_score = 0

    throws = 0
    while p1_score < 1000 and p2_score < 1000:
        p1 = next_position(p1 + next_dice(dice))
        p1_score += p1
        dice = cyclic(dice + 3, 100)
        throws += 3
        if p1_score >= 1000:
            break

        p2 = next_position(p2 + next_dice(dice))
        p2_score += p2
        dice = cyclic(dice + 3, 100)
        throws += 3

    return throws * min(p1_score, p2_score)


def parse(text):
    line1, line2 = text.splitlines()
    p1 = re.search("Player \\d+ starting position: (\\d+)", line1).groups()[0]
    p2 = re.search("Player \\d+ starting position: (\\d+)", line2).groups()[0]
    return int(p1), int(p2)


def solve1(text):
    p1, p2 = parse(text)
    return roll(p1, p2)


@cache
def quantum_dice_roll(p1, p2, p1_score, p2_score, remaining_throws, change_player):
    if p1_score >= 21:
        return 1, 0
    if p2_score >= 21:
        return 0, 1

    w = (0, 0)

    if not change_player:
        if remaining_throws == 0:
            wins = quantum_dice_roll(p1, p2, p1_score + p1, p2_score, 3, not change_player)
            w = (w[0] + wins[0], w[1] + wins[1])
        else:
            for i in range(1, 4):
                wins = quantum_dice_roll(next_position(p1 + i), p2, p1_score, p2_score, remaining_throws - 1,
                                         change_player)
                w = (w[0] + wins[0], w[1] + wins[1])
    else:
        if remaining_throws == 0:
            wins = quantum_dice_roll(p1, p2, p1_score, p2_score + p2, 3, not change_player)
            w = (w[0] + wins[0], w[1] + wins[1])
        else:
            for i in range(1, 4):
                wins = quantum_dice_roll(p1, next_position(p2 + i), p1_score, p2_score, remaining_throws - 1,
                                         change_player)
                w = (w[0] + wins[0], w[1] + wins[1])

    return w


def solve2(text):
    p1, p2 = parse(text)
    total = quantum_dice_roll(p1, p2, 0, 0, 3, False)
    return max(total[0], total[1])


f = open("../../resources/day21.txt")
puzzle = f.read()

print(solve1(example))
print(solve2(example))

print(solve1(puzzle))
print(solve2(puzzle))
