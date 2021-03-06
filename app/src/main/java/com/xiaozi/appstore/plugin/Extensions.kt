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
fun Context.dp2px(dpValue: Int) = (dpValue * resources.displayMetrics.density + 0.5f).toInt()


fun <T : View> View.bind(id: Int) = lazy { findViewById<T>(id) }

fun View.onClick(clk: () -> Unit) = setOnClickListener { clk() }

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

fun <T> Collection<T?>.toNotNullMutableList(): MutableList<T> = ArrayList<T>(this.filter { it != null })

fun <T> Iterable<T>.forkList(predicate: T.() -> Boolean, result: (resultTrue: List<T>, resultFalse: List<T>) -> Unit) {
    val tList = mutableListOf<T>()
    val fList = mutableListOf<T>()
    this.forEach { if (it.predicate()) tList.add(it) else fList.add(it) }
    result(tList, fList)
}