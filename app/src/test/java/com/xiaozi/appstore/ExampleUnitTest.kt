package com.xiaozi.appstore

import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun gsonTest() {
        val a = Gson().fromJson("{}", AAA::class.java)
        assertEquals(a.a, 0)
        assertEquals(a, null)
        println(a)
    }
    data class AAA(val a: Int, val b: String)
}
