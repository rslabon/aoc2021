import operator
from functools import reduce


def binary_str(hex_str):
    return "".join([bin(int(h, 16))[2::].zfill(4) for h in hex_str])


class Packet:

    def __init__(self, value):
        self.value = value

    def take(self, n):
        val = self.value[0:n]
        self.value = self.value[n::]
        return val

    def take_int(self, n):
        v = self.take(n)
        return int(v, 2)

    def empty(self):
        return len(self.value) == 0 or "1" not in self.value


def count_version(command):
    command_type = command["type"]
    command_version = command["version"]
    if command_type == "literal":
        return command_version
    else:
        return command_version + reduce(operator.add, map(count_version, command["args"]))


def compute(command):
    command_type = command["type"]
    if command_type == "literal":
        return command["value"]

    command_args = command["args"]
    if command_type == "sum":
        return sum(map(lambda i: compute(i), command_args))

    if command_type == "product":
        p = 1
        for c in command_args:
            p *= compute(c)

        return p

    if command_type == "min":
        return min(map(lambda i: compute(i), command_args))

    if command_type == "max":
        return max(map(lambda i: compute(i), command_args))

    if command_type == "lt":
        if compute(command_args[0]) < compute(command_args[1]):
            return 1
        else:
            return 0

    if command_type == "gt":
        if compute(command_args[0]) > compute(command_args[1]):
            return 1
        else:
            return 0

    if command_type == "eq":
        if compute(command_args[0]) == compute(command_args[1]):
            return 1
        else:
            return 0


def decode(packet, n=None):
    i = 0
    result = []
    while not packet.empty():
        version = packet.take_int(3)
        type_id = packet.take_int(3)

        if type_id == 4:
            val = ""
            done = False
            while not done:
                group = packet.take(5)
                if group[0] == "0":
                    done = True

                val += group[1::]
            val = int(val, 2)
            result.append({"type": "literal", "version": version, "value": val})
        if type_id != 4:
            length_id = packet.take_int(1)
            subs = []
            if length_id == 0:
                total_length = packet.take_int(15)
                subs = decode(Packet(packet.take(total_length)))
            if length_id == 1:
                number_of_sub = packet.take_int(11)
                subs = decode(packet, number_of_sub)
            if type_id == 0:
                result.append({"type": "sum", "version": version, "args": subs})
            if type_id == 1:
                result.append({"type": "product", "version": version, "args": subs})
            if type_id == 2:
                result.append({"type": "min", "version": version, "args": subs})
            if type_id == 3:
                result.append({"type": "max", "version": version, "args": subs})
            if type_id == 5:
                result.append({"type": "gt", "version": version, "args": subs})
            if type_id == 6:
                result.append({"type": "lt", "version": version, "args": subs})
            if type_id == 7:
                result.append({"type": "eq", "version": version, "args": subs})

        i += 1
        if n is not None and i == n:
            return result

    return result


def solve1(text):
    text = text.strip()
    return count_version(decode(Packet(binary_str(text)))[0])


def solve2(text):
    text = text.strip()
    return compute(decode(Packet(binary_str(text)))[0])


f = open("../../resources/day16.txt")
print(solve1(f.read()))

f = open("../../resources/day16.txt")
print(solve2(f.read()))
