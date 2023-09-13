package com.thryan.secondclass.core.result

data class UserInfo(
    val id: String,
    val name: String,
    val sex: Int,
    val score: Double,
    val item: Int,
    val integrity_value: Int,
    val activity: Int
)

infix operator fun User.plus(scoreInfo: ScoreInfo): UserInfo {
    return UserInfo(
        this.id,
        this.name,
        this.sex,
        scoreInfo.score,
        scoreInfo.item,
        scoreInfo.integrity_value,
        scoreInfo.activity
    )
}