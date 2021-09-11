#include "stdafx.h"
#include "/home/hhd/project/logsloth/cpp-sample/src/logsloth.h"

////////////////////////////////////////////////////////////////////////////////
// MY SDK 오픈 헤더

typedef void (*PFUNC_MYSDK_CALLBACK) (int nErrCode, std::string strErrMsg);
void MySdkFunc(PFUNC_MYSDK_CALLBACK pCallbackFunc);







////////////////////////////////////////////////////////////////////////////////
// MY SDK 내부

void Func1(int id);
void Func2();
void Func3();
void Job1();
void Job2();
void Job3();
void Job4();
void Job5();



static PFUNC_MYSDK_CALLBACK s_pCallbackFunc = nullptr;

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

void Func2()
{
    LOGSLOTH_ENTER();
    Func3();
}

void Func3()
{
    LOGSLOTH_ENTER();
    Job1();
    auto criminal = std::chrono::system_clock::now().time_since_epoch().count() % 5;
    LOGSLOTH_VALUE("criminal", (int)criminal);

    if (criminal == 0) 
    {
        Job2();
        Job3();
        Job4();
        s_pCallbackFunc(-1, "fail, criminal found !!!");
        return;
    }

    Job5();
    s_pCallbackFunc(0, "success");
}

void Job1()
{
    LOGSLOTH_ENTER();
}

void Job2()
{
    LOGSLOTH_ENTER();
}

void Job3()
{
    LOGSLOTH_ENTER();
    std::this_thread::sleep_for(std::chrono::seconds(3));
}

void Job4()
{
    LOGSLOTH_ENTER();
}

void Job5()
{
    LOGSLOTH_ENTER();
}






////////////////////////////////////////////////////////////////////////////////
// MY SDK 사용 개발자

void MySdkFuncCallback(int nErrCode, std::string strErrMsg);

TEST(use_MySdkFunc, use_MySdkFunc)
{
    LOGSLOTH_ENTER();
    MySdkFunc(MySdkFuncCallback);
}

void MySdkFuncCallback(int nErrCode, std::string strErrMsg)
{
    LOGSLOTH_ENTER();
    LOGSLOTH_VALUE("nErrCode", nErrCode);
    LOGSLOTH_VALUE("strErrMsg", strErrMsg);
}