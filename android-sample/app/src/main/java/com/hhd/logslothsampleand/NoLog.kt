package com.hhd.logslothsampleand

class NoLog {
    fun start() {
        a()
        b()
    }

    fun a() {
        a_0()
        a_1()
    }

    fun b() {
        b_0()
        b_1()
    }

    fun a_0() {
        a_0_0()
        a_0_1()
    }

    fun a_1() {
        a_1_0()
        a_1_1()
    }

    fun b_0() {
        b_0_0()
        b_0_1()
    }

    fun b_1() {
        b_1_0()
        b_1_1()
    }

    fun a_0_0() {
    }

    fun a_0_1() {
    }

    fun a_1_0() {
    }

    fun a_1_1() {
        Thread.sleep(5000)
    }

    fun b_0_0() {
    }

    fun b_0_1() {
    }

    fun b_1_0() {
    }

    fun b_1_1() {
    }
}