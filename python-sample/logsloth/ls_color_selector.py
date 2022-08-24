# -*- coding: utf-8 -*-


from datetime import datetime, timedelta
from enum import Enum, auto
import colorama
import inspect
import json
import numpy as np
import os
import pandas as pd
import tabulate as tb
import threading
import time


class LsColorSelector:
    _COLOR_LIST = [
        colorama.Fore.RED,
        colorama.Fore.GREEN,
        colorama.Fore.YELLOW,
        colorama.Fore.BLUE,
        colorama.Fore.MAGENTA,
        colorama.Fore.CYAN,
        colorama.Fore.LIGHTBLACK_EX,
        colorama.Fore.LIGHTRED_EX,
        colorama.Fore.LIGHTGREEN_EX,
        colorama.Fore.LIGHTYELLOW_EX,
        colorama.Fore.LIGHTBLUE_EX,
        colorama.Fore.LIGHTMAGENTA_EX,
        colorama.Fore.LIGHTCYAN_EX,
    ]

    def __init__(self):
        self._idx = 0

    def next(self):
        clr = self._COLOR_LIST[self._idx]
        self._idx = (self._idx + 1) % len(LsColorSelector._COLOR_LIST)
        return clr