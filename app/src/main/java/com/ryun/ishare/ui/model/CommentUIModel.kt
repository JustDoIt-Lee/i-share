package com.ryun.ishare.ui.model

import com.ryun.ishare.ui.state.CommentMenuState
import com.ryun.ishare.ui.state.MenuType

data class CommentUIModel(
    val ideaId: String,
    val index: Int,
    val content: String,
    val nickname: String,
    val userId: String,
    val authorId: String,
    val timestamp: Long,
    val likeCount: Int,
    val replyCount: Int = 0,
    val isReply: Boolean = false,
    val prefix: String? = null,
    val menuState: CommentMenuState,
    val menuType: MenuType // ✅ 추가됨
)

val CommentUIModel.isMenuExpanded: Boolean
    get() = menuState is CommentMenuState.Expanded
