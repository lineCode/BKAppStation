package com.xiaozi.appstore.plugin

import android.content.Context
import android.os.Handler
import android.widget.Toast
import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-2.
 */
fun Call(delay: Long = 0, success: () -> Unit) = Framework._H.postDelayed(success, delay)

fun Context.ZToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

inline fun <T> T?.safety(action: T.() -> Any?) = try {
    this?.action()
} catch (ex: Exception) {
    ex.printStackTrace()
}

inline fun <T> T?.safetyNullable(action: T?.() -> Any?) = try {
    this.action()
} catch (ex: Exception) {
    ex.printStackTrace()
}

inline fun <T, R> T?.safetyRun(action: T.() -> R): R? = try {
    this?.action()
} catch (ex: Exception) {
    ex.printStackTrace()
    null
}

inline fun <reified T> T?.safetySelf(action: T.() -> Any?): T? = try {
    this?.action()
    this
} catch (ex: Exception) {
    ex.printStackTrace()
    this
}