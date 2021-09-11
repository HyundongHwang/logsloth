package com.hhd.logsloth

fun<T> logsloth(f : ()->T): T {
    LogSloth.enter()
    val res = f.invoke()
    LogSloth.leave()
    return res
}
