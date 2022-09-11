# -*- coding: utf-8 -*-


from .common_import import *


class LsHookBase:
    def on_log(self, now_dt: datetime, now_dt_str: str, file_name: str, line_num: int, func_name: str, log: str):
        pass
