package com.thryan.secondclass

import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.Webvpn
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.utils.after
import com.thryan.secondclass.core.utils.before
import com.thryan.secondclass.core.utils.success
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun ser() {
        val json = Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
        }


        val jsonString = """
        {"success":true,"code":200,"message":"请求成功","data":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjE1ODA1MDkwNjQyOTc3MDk1NzAsImFjY291bnQiOiIyMDIyMTAxMDYzIiwidXVpZCI6IjE0ZTgyMzAyLTU5NjYtNGE0Yi04ZDZkLTkxNDEwNmExYjU1NCIsInN1YiI6IjE1ODA1MDkwNjQyOTc3MDk1NzAiLCJpYXQiOjE2ODY0ODA3MjMsImV4cCI6MTY4NjU2NzEyM30.jMlvB9VEGa9OP9pp-GeU-ZTgQUBQZ25Z3zsC3IKJ65HXqr4ymVonNFeY89npNe4MbV6yioGLBaD5UNh374ZVQQ"}
    """.trimIndent()
        val project: HttpResult<String> = json.decodeFromString(jsonString)
        println(project)
    }

    @Test
    fun web() = runBlocking {
        val re = Webvpn().login("2022101063", "thryan0829")
        if (re.success()) {
            println(re.message + " " + re.data)
        } else {
            println(re.message)
        }
    }

    @Test
    fun checkWebvpn() = runBlocking {
        val res = Webvpn.checkLogin("de41eb1f56b0b106")
        println(res)
    }


    @Test
    fun secondClass() = runBlocking {
        val secondClass = SecondClass("de41eb1f56b0b106")
        val res = secondClass.login("2022101063")
        println(res.message)
        if (res.success()) {
            println(res.data)
            val activity = secondClass.getActivities()
            if (activity.success())
                println(activity.data.rows.size)
        }
        val us = secondClass.getUser()
        println(us)
        val score = secondClass.getScoreInfo(us.data)
        println(score)

    }


    @Test
    fun time() = runBlocking {
        println("2023-06-11 00:00:00".after(10))
        println("2023-06-11 00:00:00".before(10))
    }

}