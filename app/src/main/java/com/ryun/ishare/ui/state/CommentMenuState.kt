package com.ryun.ishare.ui.state

sealed class CommentMenuState {
    data object Collapsed : CommentMenuState()
    data object Expanded : CommentMenuState()
    data object TimeOnly : CommentMenuState() // ← 추가됨
}

enum class MenuType {
    COMMENT, REPLY
}