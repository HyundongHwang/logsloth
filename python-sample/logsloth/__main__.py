# -*- coding: utf-8 -*-

import sys

USAGE = ""
# USAGE += hhdpy.typora.USAGE
# USAGE += hhdpy.jupyter.USAGE
# USAGE += hhdpy.tex.USAGE


def main():
    if len(sys.argv) < 2:
        print("\nUSAGE : {}".format(USAGE))
        exit(0)

    cmd = sys.argv[1]
    args = sys.argv[2:]

    # if cmd == "typora_new_file":
    #     hhdpy.typora.new_file(args)
    # else:
    #     print("\n'{}' is unknown command!!!\nUSAGE : {}".format(cmd, USAGE))

if __name__ == "__main__":
    sys.exit(main())
