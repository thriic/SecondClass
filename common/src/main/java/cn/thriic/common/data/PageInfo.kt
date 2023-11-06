package cn.thriic.common.data

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(val totalPage: Int, val totalRows: Int, val rows: List<SCActivity>)