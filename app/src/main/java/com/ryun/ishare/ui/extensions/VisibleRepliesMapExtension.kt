package com.ryun.ishare.ui.extensions

fun Map<Pair<String, Int>, Boolean>.isReplyVisible(ideaId: String, commentIndex: Int): Boolean {
    return getOrDefault(ideaId to commentIndex, false)
}