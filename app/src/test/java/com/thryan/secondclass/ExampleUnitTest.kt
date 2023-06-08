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
    fun json() {
        val json = JSONObject("{\"a\": \"s\",\"b\": {\"c\": 1}}")
        val a = json.get("a")
        if(a is String) println("true")
    }

    @Test
    fun web() = runBlocking {
        val re = Webvpn().login("2022101063", "thryan0829")
        if(re.success) {
            println(re.message + " " + re.result!!.value)
        }else{
            println(re.message)
        }
    }


    @Test
    fun secondClass() = runBlocking {
        val secondClass = SecondClass("98088769406e58fe")
//        val res = secondClass.login("2022101063")
//        println(res.message)
//        println(res.data)
        secondClass.token =
            "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjE1ODA1MDkwNjQyOTc3MDk1NzAsImFjY291bnQiOiIyMDIyMTAxMDYzIiwidXVpZCI6ImM2YWVlODhlLTk4YmItNGNhYS04YmYzLTEyODc1NjYwZGI4ZSIsInN1YiI6IjE1ODA1MDkwNjQyOTc3MDk1NzAiLCJpYXQiOjE2ODYwNjcxMTMsImV4cCI6MTY4NjE1MzUxM30.qu7sdBhO68mOdNXATOaH3skJy3u-IxtVEhx-LDUDV_JnFGASibi8ofRs9KinL6KGix6H75gm4fTCzU07ZVYR9g"

//        val us = secondClass.getUser()
//        println(us)
//        val score = secondClass.getScoreInfo(us)
//        println(score)
//        val activity = secondClass.getMyActivities()
//        println(activity.size)

    }

    @Test
    fun time() = runBlocking {
        println("2023-06-11 00:00:00".after(10))
        println("2023-06-11 00:00:00".before(10))

    }

}