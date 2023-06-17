package com.thryan.secondclass.ui.info

import com.thryan.secondclass.core.SecondClass
import com.thryan.secondclass.core.result.SCActivity
import kotlinx.coroutines.flow.MutableStateFlow

object Repository {

    val activities: MutableStateFlow<List<SCActivity>> = MutableStateFlow(emptyList())
    var secondClass: SecondClass? = null
    fun getActivity(id: String): SCActivity? {
        activities.value.forEach {
            if (id == it.id) return it
        }
        return null
    }
    suspend fun setActivity(id:String, isSign:String){
        activities.emit(activities.value.map {
            if(it.id == id) return@map it.copy(isSign = isSign)
            else return@map it
        })
    }
}