package com.hhd.logslothsampleand

import com.hhd.logsloth.logsloth

class UseLogSloth {
    fun start() = logsloth {
        a()
        b()
    }

    fun a() = logsloth {
        a_0()
        a_1()
    }

    fun b() = logsloth {
        b_0()
        b_1()
    }

    fun a_0() = logsloth {
        a_0_0()
        a_0_1()
    }

    fun a_1() = logsloth {
        a_1_0()
        a_1_1()
    }

    fun b_0() = logsloth {
        b_0_0()
        b_0_1()
    }

    fun b_1() = logsloth {
        b_1_0()
        b_1_1()
    }

    fun a_0_0() = logsloth {
    }

    fun a_0_1() = logsloth {
    }

    fun a_1_0() = logsloth {
    }

    fun a_1_1() = logsloth {
        Thread.sleep(5000)
    }

    fun b_0_0() = logsloth {
    }

    fun b_0_1() = logsloth {
    }

    fun b_1_0() = logsloth {
    }

    fun b_1_1() = logsloth {
    }
}