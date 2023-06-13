package com.thryan.secondclass.ui.info

import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.SCActivity
import kotlinx.coroutines.flow.MutableStateFlow

object Repository {
    val activities = MutableStateFlow(emptyList<SCActivity>())
    lateinit var secondClass: SecondClass

    fun getActivity(id: String): SCActivity? {
        activities.value.forEach {
            if (id == it.id) return it
        }
        return null
    }
}