package com.ryun.ishare.util

import com.ryun.ishare.model.User

val userMap = mutableMapOf<String, User>(
    "user_1" to User(id = "user_1", nickname = "강륜"),
    "user_2" to User(id = "user_2", nickname = "유저2")
)

object UserManager {
    var currentUserId: String = "user_123" // 임시로 고정
    var currentUserNickname: String = "이강륜"

    fun getNickname(userId: String): String {
        return userMap[userId]?.nickname ?: "알 수 없음"
    }
}
