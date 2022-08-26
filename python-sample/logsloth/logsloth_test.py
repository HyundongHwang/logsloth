# -*- coding: utf-8 -*-


try:
    from logsloth import *
    from ls_hook_base import *
except Exception as ex:
    from .logsloth import *
    from .ls_hook_base import *


class LogSlothTest:
    @LogSloth.fun_scope
    def test_basic(self):
        LogSloth.d("hello")
        LogSloth.caller("cmd123")

        for _ in range(5):
            LogSloth.d("work ...")

        LogSloth.callee("cmd123")
        LogSloth.d("world")

        my_dic = {}
        my_dic["a"] = 123
        my_dic["456"] = "bcd"
        my_dic["efg"] = 7.8
        LogSloth.d(f"my_dic:\n{LogSloth.to_str(my_dic)}")


class MyHook(LsHookBase):
    def on_log(self, now_dt: datetime, now_dt_str: str, file_name: str, line_num: int, func_name: str, log: str):
        print(f"on_log")


if __name__ == '__main__':
    LogSloth.show_color = True
    LogSloth.use_detail_foramt = False
    LogSloth.add_hook(MyHook())
    LogSlothTest().test_basic()
