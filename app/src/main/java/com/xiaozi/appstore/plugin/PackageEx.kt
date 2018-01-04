package com.xiaozi.appstore.plugin

import android.util.Log

/**
 * Created by fish on 18-1-2.
 */
val _DEBUG = true


private val _LOG_D = true
private val _LOG_W = true
private val _LOG_E = true

fun ZLogD(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_D) Log.d(tag, msg) }
fun ZLogW(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_W) Log.w(tag, msg) }
fun ZLogE(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_E) Log.e(tag, msg) }

inline fun <T, R> within(t: T, r: T.() -> R) = t.run(r)