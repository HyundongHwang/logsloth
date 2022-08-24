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
    def on_log(self, dt_now: datetime, now_date_time: str, file_name: str, line_num: int, func_name: str, log: str):
        pass
