package com.thryan.secondclass.core.result

import java.math.BigDecimal

/**
 * @param score 积分
 * @param item 申报活动数？
 * @param integrity_value 诚信值
 * @param activity 完成活动数
 */
data class ScoreInfo(
    val score: Double, val item: Int, val integrity_value: Int,
    val activity: Int
)
