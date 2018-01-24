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

    @Test
    fun checkChild() {
        if (null.equals(null))
        print(getSubStrPositions("#哈哈##哈哈##哈哈#", "#哈哈#"))
    }

    private fun getSubStrPositions(originStr: String, sub: String): List<Int>? {
        if (!originStr.contains(sub)) return null
        val result = mutableListOf<Int>()
        var ptr = 0
        println(originStr.split(sub))
        originStr.split(sub).map {
            ptr += it.length
            result.add(ptr)
            ptr += sub.length
        }
        result.removeAt(result.size - 1)
        return result
    }


    data class AAA(val a: Int, val b: String)
}
