import os
import sys

source_file = ""
destination_file = ""
replacements = []

for arg in sys.argv:
    index = sys.argv.index(arg)
    if index == 1:
        source_file = arg
    if index == 2:
        destination_file = arg
    if index >= 3:
        replacements.append(arg)

if os.path.isfile(source_file):
    print("Wiping:")
    print("From: " + source_file)
    print("Into: " + destination_file)

    replace_what = []
    replace_with = []
    for x in range(0, replacements.__len__()):
        if x % 2 == 0:
            replace_what.append(replacements[x])
        else:
            replace_with.append(replacements[x])

    with open(source_file, "rt") as fin:
        with open(destination_file, "wt") as fout:
            for line in fin:
                replaced = line
                for x in range(0, replacements.__len__() / 2):
                    replaced = replaced.replace(replace_what[x], replace_with[x])
                fout.write(replaced)

    print("-----")
