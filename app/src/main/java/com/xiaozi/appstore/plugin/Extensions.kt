package com.xiaozi.appstore

import android.content.Context
import android.view.View
import android.widget.Toast
import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-2.
 */
fun Call(delay: Long = 0, success: () -> Unit) = Framework._H.postDelayed(success, delay)

fun Context.ZToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun <T : View> View.bind(id: Int) = lazy { findViewById<T>(id) }

inline fun <T, R> T?.safety(action: T.() -> R): R? = try {
    this?.action()
} catch (ex: Exception) {
    ex.printStackTrace()
    null
}

inline fun <T, R> T?.safetyNullable(action: T?.() -> R?): R? = try {
    this.action()
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