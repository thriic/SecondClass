package com.thryan.secondclass

import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.Webvpn
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun web() = runBlocking {
        val re = Webvpn().login("2022101063", "")
        if (re.success) {
            println(re.message + " " + re.value!!)
        } else {
            println(re.message)
        }
    }
    @Test
    fun checkWebvpn() = runBlocking {
        val res = Webvpn.checkLogin("ea718a1e337fc988")
        println(res.value!!)
    }


    @Test
    fun secondClass() = runBlocking {
        val secondClass = SecondClass("ea718a1e337fc988")
        val res = secondClass.login("2022101063")
        println(res.message)
        if (res.success) {
            println(res.value!!)
            val activity = secondClass.getMyActivities()
            if(activity.success)
            println(activity.value!!.size)
        }
        val us = secondClass.getUser()
        println(us)
        val score = secondClass.getScoreInfo(us.value!!)
        println(score)

    }


    @Test
    fun time() = runBlocking {
        println("2023-06-11 00:00:00".after(10))
        println("2023-06-11 00:00:00".before(10))
    }

}