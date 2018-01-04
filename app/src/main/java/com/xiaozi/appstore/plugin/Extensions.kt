package com.xiaozi.appstore.plugin

import android.content.Context
import android.os.Handler
import android.widget.Toast

/**
 * Created by fish on 18-1-2.
 */
fun Handler.call(delay: Long = 0, success: ()->Unit) = postDelayed(success, delay)
fun Context.ZToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

inline fun <T> T?.safety(action: T.() -> Any?) = try {
    this?.action()
} catch (ex: Exception) {
    ex.printStackTrace()
}

inline fun <T, R> T?.safetyRun(action:T.() -> R): R? = try {
    this?.action()
} catch(ex: Exception) {
    ex.printStackTrace()
    null
}