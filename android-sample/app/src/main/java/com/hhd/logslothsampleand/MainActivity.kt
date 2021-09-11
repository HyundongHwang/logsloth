package com.hhd.logslothsampleand

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hhd.logsloth.LogSloth
import com.hhd.logsloth.logsloth
import com.hhd.logslothsampleand.util.MyActivityUtil
import com.hhd.logslothsampleand.util.MyUtil
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyActivityUtil.setActionBarHide(this)
        MyActivityUtil.setKeepScreenOn(this)
        val context = this as Context
        val thisObj = this
        val sv = MyUtil.createScrollViewMpWc(context)
        val fl = MyUtil.createFlexboxLayoutMpWc(context)
        this.setContentView(sv)
        sv.addView(fl)

        this.javaClass.methods
            .filter { it.name.startsWith("_") }
            .filterNot { it.name.contains("$") }
            .forEach {
                val method = it
                val btn = Button(context)
                fl.addView(btn)
                btn.isAllCaps = false
                btn.text = method.name
                btn.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        method.invoke(thisObj)
                    }
                }
            }
    }

    fun _00_simple_LogSloth() = logsloth {
        LogSloth.d("hello world")
        LogSloth.caller("cmd123")
        repeat(5) {
            LogSloth.d("work ...")
        }
        LogSloth.callee("cmd123")
    }

    fun _01_no_log() {
        NoLog().start()
    }

    fun _02_use_LogCat() {
        UseLogCat().start()
    }

    fun _03_use_LogSloth() {
        UseLogSloth().start()
    }

    fun _04_multi_thread() {
        repeat(2) {
            GlobalScope.launch {
                UseLogSloth().start()
            }
        }
    }
}

