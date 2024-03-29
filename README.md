# LogSloth
<img width="200" src="https://user-images.githubusercontent.com/5696570/132941847-3193d9c9-3675-4d04-bb59-ac3eb901193c.png"/>

## Introduction
- Utility to help analyze execution structure and performance bottleneck of complex applications by using pure and simple text log.

## Target User
- Analysis of execution flow and call relationship of new project
- Detailed analysis of performance by section

## Additional usage scenarios
- Analyze the code in detail by lying down and pressing the arrow keys with the iPad
- Dynamic document that feels like recording the debugging screen while taking a breakpoint

## Includings
- Text Logger Library
  - python
  - java/kotlin
  - cpp
- Log Viewer
  - web
  - powershell

## Text log's essentials + alpha

- Essentials
  - Timestamp: Year Month Day + Hour Minute Seconds + Milliseconds
  - PID
  - TID
  - free text
- alpha
  - file name
  - line number
  - function name
  - thread name
  - Scope entry/exit
  - Thread work request/work reception
  - Variable name/variable value

<br/>
<br/>
<br/>

## Logger Installation, Writting logs, Collecting logs

### python

- Installation
  - `pip install logsloth`

- Writting logs
```python
import logsloth

class LogSlothTest:
    @logsloth.fun_scope
    def test_basic(self):
        logsloth.d("hello")
        logsloth.caller("cmd123")

        for _ in range(5):
            logsloth.d("work ...")

        logsloth.callee("cmd123")
        logsloth.d("world")

if __name__ == '__main__':
    logsloth.init(show_color=True)
    LogSlothTest().test_basic()
```

- Collecting logs
![](https://i.imgur.com/XdEWWci.png)

<br/>
<br/>
<br/>



### java, kotlin
[![](https://jitpack.io/v/HyundongHwang/logsloth.svg)](https://jitpack.io/#HyundongHwang/logsloth)

- Installation

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.HyundongHwang:logsloth:0.0.0'
}
```

- Writting logs

```kotlin
fun _00_simple_LogSloth() = logsloth {
    LogSloth.d("hello world")
    LogSloth.caller("cmd123")
    repeat(5) {
        LogSloth.d("work ...")
    }
    LogSloth.callee("cmd123")
}
```

- Collecting logs

```text
$ adb logcat -c
$ adb logcat | grep --line-buffered LOGSLOTH
09-11 17:43:04.835 21683 21683 D LOGSLOTH: MainActivity.kt:43:_00_simple_LogSloth main ↘↘↘
09-11 17:43:04.835 21683 21683 D LOGSLOTH: MainActivity.kt:44:_00_simple_LogSloth main hello world
09-11 17:43:04.836 21683 21683 D LOGSLOTH: MainActivity.kt:45:_00_simple_LogSloth main →→→cmd123→→→
09-11 17:43:04.836 21683 21683 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
09-11 17:43:04.836 21683 21683 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
09-11 17:43:04.836 21683 21683 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
09-11 17:43:04.837 21683 21683 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
09-11 17:43:04.837 21683 21683 D LOGSLOTH: MainActivity.kt:47:_00_simple_LogSloth main work ...
09-11 17:43:04.837 21683 21683 D LOGSLOTH: MainActivity.kt:49:_00_simple_LogSloth main ←←←cmd123←←←
09-11 17:43:04.837 21683 21683 D LOGSLOTH: MainActivity.kt:43:_00_simple_LogSloth main ↗↗↗
```

<br/>
<br/>
<br/>

### c++

- Installation

```cpp
#include "/home/hhd/project/logsloth/cpp-sample/src/logsloth.h"
```

- Writting logs

```cpp
#include "/home/hhd/project/logsloth/cpp-sample/src/logsloth.h"

void MySdkFunc(PFUNC_MYSDK_CALLBACK pCallbackFunc)
{
    LOGSLOTH_ENTER();
    LOGSLOTH_VALUE_PTR("pCallbackFunc", (void *) pCallbackFunc);
    s_pCallbackFunc = pCallbackFunc;
    std::vector<std::shared_ptr<std::thread>> vThread;

    for(size_t i = 0; i < 3; i++)
    {
        LOGSLOTH_VALUE("i", (int)i);
        auto t = std::make_shared<std::thread>(Func1, i);
        LOGSLOTH_CALLER("Func1_%d", i);
        vThread.push_back(t);
    }

    for(auto pThread : vThread)
    {
        pThread->join();
    }
}

void Func1(int id)
{
    LOGSLOTH_ENTER();
    LOGSLOTH_CALLEE("Func1_%d", id);
    Func2();
}

void Job1()
{
    LOGSLOTH_ENTER();
}

void Job2()
{
    LOGSLOTH_ENTER();
}

```

- Collecting logs
```text
[==========] Running 1 test from 1 test case.
[----------] Global test environment set-up.
[----------] 1 test from use_MySdkFunc
[ RUN      ] use_MySdkFunc.use_MySdkFunc
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:124:TestBody ↘↘↘
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:34:MySdkFunc ↘↘↘
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:35:MySdkFunc pCallbackFunc:::0x458deb
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:41:MySdkFunc i:::0
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:43:MySdkFunc →→→Func1_0→→→
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:41:MySdkFunc i:::1
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↘↘↘
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:56:Func1 ←←←Func1_0←←←
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↘↘↘
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↘↘↘
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↘↘↘
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↗↗↗
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:71:Func3 criminal:::2
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:109:Job5 ↘↘↘
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:109:Job5 ↗↗↗
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↘↘↘
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:43:MySdkFunc →→→Func1_1→→→
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:56:Func1 ←←←Func1_1←←←
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:71:Func3 criminal:::3
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:109:Job5 ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:109:Job5 ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↘↘↘
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:131:MySdkFuncCallback nErrCode:::0
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:41:MySdkFunc i:::2
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:131:MySdkFuncCallback nErrCode:::0
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:132:MySdkFuncCallback strErrMsg:::success
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↗↗↗
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↗↗↗
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↗↗↗
07-03 17:49:30.325 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↘↘↘
07-03 17:49:30.325 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:56:Func1 ←←←Func1_2←←←
07-03 17:49:30.325 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:43:MySdkFunc →→→Func1_2→→→
07-03 17:49:30.325 4484 -868255325 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:132:MySdkFuncCallback strErrMsg:::success
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↗↗↗
07-03 17:49:30.325 4484 -1019556935 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↗↗↗
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↘↘↘
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↘↘↘
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↘↘↘
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:88:Job1 ↗↗↗
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:71:Func3 criminal:::0
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:93:Job2 ↘↘↘
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:93:Job2 ↗↗↗
07-03 17:49:30.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:98:Job3 ↘↘↘
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:98:Job3 ↗↗↗
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:104:Job4 ↘↘↘
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:104:Job4 ↗↗↗
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↘↘↘
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:131:MySdkFuncCallback nErrCode:::-1
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:132:MySdkFuncCallback strErrMsg:::fail, criminal found !!!
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:130:MySdkFuncCallback ↗↗↗
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:68:Func3 ↗↗↗
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:62:Func2 ↗↗↗
07-03 17:49:33.326 4484 -2068248158 D LOGSLOTH: test_enter_first.cpp:55:Func1 ↗↗↗
07-03 17:49:33.326 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:34:MySdkFunc ↗↗↗
07-03 17:49:33.326 4484 -2060011887 D LOGSLOTH: test_enter_first.cpp:124:TestBody ↗↗↗
[       OK ] use_MySdkFunc.use_MySdkFunc (3001 ms)
[----------] 1 test from use_MySdkFunc (3001 ms total)

[----------] Global test environment tear-down
[==========] 1 test from 1 test case ran. (3002 ms total)
[  PASSED  ] 1 test.
```

<br/>
<br/>
<br/>

## LogViewer

### powershell

- Installation

`Install-Module -Name logsloth`

- Analysing logs

`PS> cat my.log | logsloth`

<img width="600" src="https://i.imgur.com/1nPRD19.png"/>


<br/>
<br/>
<br/>

### web
- https://hyundonghwang.github.io/logsloth/logsloth.html

![](https://i.imgur.com/3YEmNP4.png)

<br/>
<br/>
<br/>


## github integration demoes
  - Analysing logs demo
    - [![Video Label](http://img.youtube.com/vi/wbvcmzYpA98/0.jpg)](https://youtu.be/wbvcmzYpA98?t=0s)
  - Code review demo
    - [![Video Label](http://img.youtube.com/vi/QkwRIX8SppI/0.jpg)](https://youtu.be/QkwRIX8SppI?t=0s)