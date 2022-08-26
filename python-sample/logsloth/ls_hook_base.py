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


class LsHookBase:
    def on_log(self, now_dt: datetime, now_dt_str: str, file_name: str, line_num: int, func_name: str, log: str):
        pass
