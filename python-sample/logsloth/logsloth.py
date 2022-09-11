# -*- coding: utf-8 -*-


from .common_import import *
from .ls_color_selector import *
from .ls_hook_base import *


class LogSloth:
    show_color = True
    use_detail_foramt = False
    timezone = "Asia/Seoul"  # UTC, America/New_York
    _hook_list = []

    _STACK_FILETER_OUT_WORD_LIST = [
        "/logsloth.py",
        "/unittest/",
        "/plugins/"
    ]

    _TO_STR_HIDDEN_COLUMN_LIST = [
        "PartitionKey",
        "Timestamp",
        "etag",
    ]

    @staticmethod
    def add_hook(hook: LsHookBase):
        LogSloth._hook_list.append(hook)

    @staticmethod
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
        now_dt = maya.now().datetime(to_timezone=LogSloth.timezone, naive=False)
        now_date = now_dt.strftime("%m-%d")
        now_time = now_dt.strftime("%H%M%S")
        now_ms = now_dt.strftime("%f")[:3]
        now_dt_str = now_dt.strftime("%y%m%d_%H%M%S")

        if file_name is None:
            st_list = inspect.stack()

            for st in st_list:
                is_my_stack = True
                for w in LogSloth._STACK_FILETER_OUT_WORD_LIST:
                    if w in st.filename:
                        is_my_stack = False
                        break
                if is_my_stack:
                    file_name = st.filename.split("/")[-1]
                    line_num = st.lineno
                    func_name = st.function
                    break

        clr = LsColorSelector()
        full_log = f""

        if LogSloth.use_detail_foramt:
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
        else:
            full_log += f"{clr.next()}{now_dt_str} "
            full_log += f"{clr.next()}{file_name}:"
            full_log += f"{clr.next()}{line_num}:"
            full_log += f"{clr.next()}{func_name} "
            full_log += f"{colorama.Fore.RESET}{log}"

        print(full_log)

        for hook in LogSloth._hook_list:
            hook: LsHookBase = hook

            hook.on_log(
                now_dt=now_dt,
                now_dt_str=now_dt_str,
                file_name=file_name,
                line_num=line_num,
                func_name=func_name,
                log=log,
            )

    @staticmethod
    def d(log):
        LogSloth._print_log(log)

    @staticmethod
    def value(name, value):
        clr = LsColorSelector()
        LogSloth._print_log(f"{clr.next()}{name}{clr.next()}:::{clr.next()}{value}{colorama.Fore.RESET}")

    @staticmethod
    def enter():
        clr = LsColorSelector()
        LogSloth._print_log(f"{clr.next()}↘↘↘{colorama.Fore.RESET}")

    @staticmethod
    def leave():
        clr = LsColorSelector()
        LogSloth._print_log(f"{clr.next()}↗↗↗{colorama.Fore.RESET}")

    @staticmethod
    def caller(anchor_name):
        clr = LsColorSelector()
        LogSloth._print_log(f"{clr.next()}→→→{clr.next()}{anchor_name}{clr.next()}→→→{colorama.Fore.RESET}")

    @staticmethod
    def callee(anchor_name):
        clr = LsColorSelector()
        LogSloth._print_log(f"{clr.next()}←←←{clr.next()}{anchor_name}{clr.next()}←←←{colorama.Fore.RESET}")

    @staticmethod
    def fun_scope(func, *args, **kwargs):
        file_name = None
        line_num = None
        func_name = str(func).split(" ")[1].split(".")[1]
        st_list = inspect.stack()

        for st in st_list:
            is_my_stack = True
            for w in LogSloth._STACK_FILETER_OUT_WORD_LIST:
                if w in st.filename:
                    is_my_stack = False
                    break
            if is_my_stack:
                file_name = st.filename.split("/")[-1]
                line_num = st.lineno
                break

        def func_wrapper(*args, **kwargs):
            clr = LsColorSelector()
            LogSloth._print_log(f"{clr.next()}↘↘↘{colorama.Fore.RESET}", file_name, line_num, func_name)
            func(*args, **kwargs)
            clr = LsColorSelector()
            LogSloth._print_log(f"{clr.next()}↗↗↗{colorama.Fore.RESET}", file_name, line_num, func_name)

        return func_wrapper

    @staticmethod
    def to_str(value, select=None):
        if value is None or \
                isinstance(value, int) or \
                isinstance(value, float) or \
                isinstance(value, str):
            return str(value)

        res_str = None
        value_type_str = str(type(value))

        if "parameters" in dir(value):
            res_str = f"{value} \n"
            for key in value.state_dict():
                param = value.state_dict()[key]
                data_str = "{}".format(param.data)
                if len(data_str) > 100:
                    data_str = data_str[:100] + " ..."
                res_str += f"{key}    {param.shape}\n    {data_str}\n"
            return res_str
        elif "torch.Tensor" in value_type_str:
            data_str = f"{value.data}"
            if len(data_str) > 100:
                data_str = data_str[:100] + " ..."
            res_str = f"{value.shape} {data_str}\n"
            return res_str

        df = None

        if isinstance(value, pd.DataFrame):
            df = value
        elif isinstance(value, list) or isinstance(value, tuple):
            dict_item_list = []
            for item in value:
                item_type_str = str(type(item))
                if item is None or \
                        isinstance(item, int) or \
                        isinstance(item, float) or \
                        isinstance(item, str):
                    dict_item_list.append(str(item))
                elif isinstance(item, dict):
                    dict_item_list.append(item)
                elif "__dict__" in dir(item):
                    dict_item_list.append(item.__dict__)
                else:
                    dict_item = {}
                    for k in dir(item):
                        if type(k) is str and k.startswith("__"):
                            continue
                        if item.get(k) is None:
                            continue
                        if k in LogSloth._TO_STR_HIDDEN_COLUMN_LIST:
                            continue
                        dict_item[k] = item[k]
                    dict_item_list.append(dict_item)
            df = pd.DataFrame(dict_item_list)
        else:
            dict_value = None

            if isinstance(value, dict):
                dict_value = value
            elif "Entity" in str(type(value)):
                dict_value = value
            elif "to_dict" in dir(value):
                dict_value = value.to_dict()
            elif "__dict__" in dir(value):
                dict_value = value.__dict__

            t_list = []
            for k in dict_value:
                if type(k) is str and k.startswith("__"):
                    continue
                if value.get(k) is None:
                    continue
                if k in LogSloth._TO_STR_HIDDEN_COLUMN_LIST:
                    continue
                t_list.append({
                    "key": k,
                    "value": value[k],
                })
            df = pd.DataFrame(t_list)

        df = df.applymap(func=LogSloth._str_format_for_tabulate)

        if select is not None:
            col_list = [col.strip() for col in select.split(",")]
            df = df[col_list]

        res_str = tb.tabulate(df, headers='keys', tablefmt='psql')
        return res_str

    @staticmethod
    def _str_format_for_tabulate(x):
        if x is None:
            return ""

        x_type_str = str(type(x)).lower()
        is_int = "int" in x_type_str
        is_float = "float" in x_type_str
        is_str = "str" in x_type_str
        is_date_time = "date" in x_type_str or "time" in x_type_str

        if is_int or is_float:
            if np.isnan(x):
                return ""
            BIG_NUM = 1e20
            x = max(min(x, BIG_NUM), -BIG_NUM)

        if is_float:
            x_diff = abs(x - int(x))
            if x_diff < 0.0001:
                x = int(x)
                is_int = True
                is_float = False

        if is_int:
            res_str = f"{x:d}"
            return res_str

        if is_float:
            res_str = f"{x:.4f}"
            res_str = res_str.rstrip("0")

            if len(res_str) > 8:
                res_str = f"{x:.0f}"

            return res_str

        if is_date_time:
            res_str = str(x)
            return res_str

        x_str = None

        if is_str:
            x_str = x
        else:
            try:
                x_str = json.dumps(x, indent=4, ensure_ascii=False)
            except:
                x_str = str(x)

        line_list = x_str.split("\n")
        new_line_list = []
        for line in line_list:
            line = line[:80]
            new_line_list.append(line)
        new_line_list = new_line_list[:100]
        res_str = "\n".join(new_line_list)
        return res_str


if __name__ == '__main__':
    breakpoint()
