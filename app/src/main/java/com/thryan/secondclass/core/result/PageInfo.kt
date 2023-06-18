package com.thryan.secondclass.core.result

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(val totalPage:Int,val totalRows:Int,val rows:List<SCActivity>)