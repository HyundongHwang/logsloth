# -*- coding: utf-8 -*-


from .common_import import *


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
        self.show_color: bool = True
        self._idx = 0

    def next(self) -> str:
        if not self.show_color:
            return ""
        clr = self._COLOR_LIST[self._idx]
        self._idx = (self._idx + 1) % len(LsColorSelector._COLOR_LIST)
        return clr

    def reset(self):
        if not self.show_color:
            return ""
        return colorama.Fore.RESET
