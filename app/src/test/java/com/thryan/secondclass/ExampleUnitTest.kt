package com.thryan.secondclass

import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.WebVpn
import com.thryan.secondclass.core.result.HttpResult
import com.thryan.secondclass.core.utils.success
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        val re = WebVpn.login("", "")
        if (re.success()) {
            println(re.message + " " + re.data)
        } else {
            println(re.message)
        }
    }

    @Test
    fun checkWebvpn() = runBlocking {
        val res = WebVpn.checkLogin("1602fd5210ef6c58")
        println(res)
    }


    @Test
    fun secondClass() = runBlocking {
        val secondClass = SecondClass("1602fd5210ef6c58")
        val res = secondClass.login("2022101063")
        println(res.message)
        if (res.success()) {
            println(res.data)
            val activity = secondClass.getActivities(1,20)
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse("2023-06-11 00:00:00", formatter)
    }

}