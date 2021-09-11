package com.hhd.logslothsampleand

import android.util.Log

class UseLogCat {
    private val TAG: String = this.javaClass.simpleName.uppercase()

    fun start() {
        Log.d(TAG, "main enter")
        a()
        b()
        Log.d(TAG, "main leave")
    }

    fun a() {
        Log.d(TAG, "a enter")
        a_0()
        a_1()
        Log.d(TAG, "a enter")
    }

    fun b() {
        Log.d(TAG, "b enter")
        b_0()
        b_1()
        Log.d(TAG, "b leave")
    }

    fun a_0() {
        Log.d(TAG, "a_0 enter")
        a_0_0()
        a_0_1()
        Log.d(TAG, "a_0 leave")
    }

    fun a_1() {
        Log.d(TAG, "a_1 enter")
        a_1_0()
        a_1_1()
        Log.d(TAG, "a_1 leave")
    }

    fun b_0() {
        Log.d(TAG, "b_0 enter")
        b_0_0()
        b_0_1()
        Log.d(TAG, "b_0 leave")
    }

    fun b_1() {
        Log.d(TAG, "b_1 enter")
        b_1_0()
        b_1_1()
        Log.d(TAG, "b_1 leave")
    }

    fun a_0_0() {
        Log.d(TAG, "a_0_0 enter")
        Log.d(TAG, "a_0_0 leave")
    }

    fun a_0_1() {
        Log.d(TAG, "a_0_1 enter")
        Log.d(TAG, "a_0_1 leave")
    }

    fun a_1_0() {
        Log.d(TAG, "a_1_0 enter")
        Log.d(TAG, "a_1_0 leave")
    }

    fun a_1_1() {
        Log.d(TAG, "a_1_1 enter")
        Thread.sleep(5000)
        Log.d(TAG, "a_1_1 leave")
    }

    fun b_0_0() {
        Log.d(TAG, "b_0_0 enter")
        Log.d(TAG, "b_0_0 leave")
    }

    fun b_0_1() {
        Log.d(TAG, "b_0_1 enter")
        Log.d(TAG, "b_0_1 leave")
    }

    fun b_1_0() {
        Log.d(TAG, "b_1_0 enter")
        Log.d(TAG, "b_1_0 leave")
    }

    fun b_1_1() {
        Log.d(TAG, "b_1_1 enter")
        Log.d(TAG, "b_1_1 leave")
    }
}