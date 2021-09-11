# -*- coding: utf-8 -*-


import datetime
import inspect
import os
import colorama
import threading

g_show_color = True


def init(show_color=True):
    global g_show_color
    g_show_color = show_color


def _print_log(log, file_name=None, line_num=None, func_name=None):
    # 08-28 14:01:51.835  9531  9531 D LOGSLOTH: MainActivity.kt:43:_00_simple_LogSloth main ↘↘↘
    # 08-28 14:01:51.835  9531  9531 D LOGSLOTH: MainActivity.kt:44:_00_simple_LogSloth main hello world
    # 08-28 14:01:51.835  9531  9531 D LOGSLOTH: MainActivity.kt:45:_00_simple_LogSloth main →→→cmd123→→→
    # 08-28 14:01:51.835  9531  9531 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
    # 08-28 14:01:51.836  9531  9531 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
    # 08-28 14:01:51.836  9531  9531 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
    # 08-28 14:01:51.836  9531  9531 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
    # 08-28 14:01:51.836  9531  9531 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
    # 08-28 14:01:51.837  9531  9531 D LOGSLOTH: MainActivity.kt:49:_00_simple_LogSloth main ←←←cmd123←←←
    # 08-28 14:01:51.837  9531  9531 D LOGSLOTH: MainActivity.kt:43:_00_simple_LogSloth main ↗↗↗

    pid = os.getpid()
    tid = threading.current_thread().native_id
    tname = threading.current_thread().name
    now = datetime.datetime.now()
    now_date = now.strftime("%m-%d")
    now_time = now.strftime("%H%M%S")
    now_ms = now.strftime("%f")[-3]

    if file_name is None:
        st_list = inspect.stack()

        for st in st_list:
            is_my_stack = True
            for w in ["hellologsloth.py", "/unittest/", "/plugins/"]:
                if w in st.filename:
                    is_my_stack = False
                    break
            if is_my_stack:
                file_name = st.filename.split("/")[-1]
                line_num = st.lineno
                func_name = st.function
                break

    clr = ColorSelector()
    full_log = f""
    full_log += f"{clr.next()}{now_date} "
    full_log += f"{clr.next()}{now_time}."
    full_log += f"{clr.next()}{now_ms} "
    full_log += f"{clr.next()}{pid} "
    full_log += f"{clr.next()}{tid} "
    full_log += f"{clr.next()}D "
    full_log += f"{clr.next()}LOGSLOTH "
    full_log += f"{clr.next()}{file_name}:"
    full_log += f"{clr.next()}{line_num}:"
    full_log += f"{clr.next()}{func_name} "
    full_log += f"{clr.next()}{tname} "
    full_log += f"{colorama.Fore.RESET}{log}"
    print(full_log)


def d(log):
    _print_log(log)


def value(name, value):
    clr = ColorSelector()
    _print_log(f"{clr.next()}{name}{clr.next()}:::{clr.next()}{value}{colorama.Fore.RESET}")


def enter():
    clr = ColorSelector()
    _print_log(f"{clr.next()}↘↘↘{colorama.Fore.RESET}")


def leave():
    clr = ColorSelector()
    _print_log(f"{clr.next()}↗↗↗{colorama.Fore.RESET}")


def caller(anchor_name):
    clr = ColorSelector()
    _print_log(f"{clr.next()}→→→{clr.next()}{anchor_name}{clr.next()}→→→{colorama.Fore.RESET}")


def callee(anchor_name):
    clr = ColorSelector()
    _print_log(f"{clr.next()}←←←{clr.next()}{anchor_name}{clr.next()}←←←{colorama.Fore.RESET}")


def fun_scope(func, *args, **kwargs):
    file_name = None
    line_num = None
    func_name = str(func).split(" ")[1].split(".")[1]
    st_list = inspect.stack()

    for st in st_list:
        is_my_stack = True
        for w in ["hellologsloth.py", "/unittest/", "/plugins/"]:
            if w in st.filename:
                is_my_stack = False
                break
        if is_my_stack:
            file_name = st.filename.split("/")[-1]
            line_num = st.lineno
            break

    def func_wrapper(*args, **kwargs):
        clr = ColorSelector()
        _print_log(f"{clr.next()}↘↘↘{colorama.Fore.RESET}", file_name, line_num, func_name)
        func(*args, **kwargs)
        clr = ColorSelector()
        _print_log(f"{clr.next()}↗↗↗{colorama.Fore.RESET}", file_name, line_num, func_name)

    return func_wrapper


class ColorSelector:
    def __init__(self):
        self._COLOR_LIST = [
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
        self._idx = 0

    def next(self):
        global g_show_color
        if g_show_color:
            clr = self._COLOR_LIST[self._idx]
            self._idx = (self._idx + 1) % len(self._COLOR_LIST)
            return clr
        else:
            return ""
