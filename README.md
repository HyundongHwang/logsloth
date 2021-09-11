# LogSloth
<img width="200" src="https://user-images.githubusercontent.com/5696570/132941847-3193d9c9-3675-4d04-bb59-ac3eb901193c.png"/>

## 소개
- 순수하고 간단한 텍스트로그를 활용해서, 복잡한 어플리케이션의 실행구조분석, 성능병목분석을 돕는 유틸리티.

## 사용대상
- 신규프로젝트의 실행흐름, 호출관계 분석
- 구간별 성능 상세분석

## 추가적인 사용 시나리오
- 누워서 아이패드로 방향키만 눌러서 코드 상세 분석
- 정지점 찍어가며 디버깅 하는 화면을 녹화한것 같은 느낌의 동적 문서 

## 포함내용
- 텍스트 로거
  - python
  - java/kotlin
  - cpp
- 로그 뷰어
  - web
  - powershell

## 텍스트 로그의 필수요소 + alpha

- 필수요소
	- 타임스탬프 : 연월일 + 시분초 + 밀리세컨드
	- PID
	- TID
	- 자유 텍스트
- alpha
	- 파일명
	- 라인번호
	- 함수이름
	- 스레드 이름
	- 스코프 진입/탈출
	- 스레드 작업요청/ 작업수신
	- 변수명/변수값

<br/>
<br/>
<br/>

## Logger 설치, 로그기록, 로그수집

### python

- 설치
  - `pip install logsloth`

- 로그기록
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

- 로그확인
![](https://i.imgur.com/XdEWWci.png)

<br/>
<br/>
<br/>



### java, kotlin
[![](https://jitpack.io/v/HyundongHwang/logsloth.svg)](https://jitpack.io/#HyundongHwang/logsloth)

- 설치

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.HyundongHwang:logsloth:0.0.0'
}
```

- 로그기록

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

- 로그확인
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

- 설치

```cpp
#include "/home/hhd/project/logsloth/cpp-sample/src/logsloth.h"
```

- 로그작성

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

- 로그확인
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

- 설치

`Install-Module -Name logsloth`

- 분석

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


## github 연동 데모
  - 로그분석 데모
    - [![Video Label](http://img.youtube.com/vi/wbvcmzYpA98/0.jpg)](https://youtu.be/wbvcmzYpA98?t=0s)
  - 코드리뷰 데모
    - [![Video Label](http://img.youtube.com/vi/QkwRIX8SppI/0.jpg)](https://youtu.be/QkwRIX8SppI?t=0s)


---