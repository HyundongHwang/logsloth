# -*- coding: utf-8 -*-

from logsloth import LogSloth


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


if __name__ == '__main__':
    LogSloth.show_color = True
    LogSloth.use_detail_foramt = False
    LogSlothTest().test_basic()
