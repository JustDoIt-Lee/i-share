package com.ryun.ishare.util

fun makeCommentKey(ideaId: String, index: Int): String {
    return "$ideaId-$index"
}