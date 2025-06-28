//package com.ryun.ishare.ui.components
//
//
//import androidx.compose.runtime.Composable
//import com.ryun.ishare.ui.components.common.CommentReplyContent
//import com.ryun.ishare.ui.state.CommentMenuState
//
//@Composable
//fun ReplyItem(
//    ideaId: String,
//    replyIndex: Int,
//    replyText: String,
//    nickname: String,
//    userId: String,
//    commentAuthorId: String,
//    timestamp: Long,
//    commentLikeCounts: Map<String, Int>,
//    menuState: CommentMenuState,
//    onLikeClick: () -> Unit,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//    onTimeToggle: () -> Unit,
//    formatRelativeTime: (Long) -> String,
//    onReplyClick: (() -> Unit)? = null,
//    onMenuStateChange: (CommentMenuState) -> Unit,
//    clearAllMenus: () -> Unit,
//    clearCommentInput: () -> Unit,
//) {
//    val replyKey = "$ideaId-$replyIndex"
//    val likeCount = commentLikeCounts[replyKey] ?: 0
//
//    CommentReplyContent(
//        ideaId = ideaId,
//        index = replyIndex,
//        nickname = nickname,
//        content = replyText,
//        userId = userId,
//        commentAuthorId = commentAuthorId,
//        timestamp = timestamp,
//        likeCount = likeCount,
//        replyCount = 0, // 답글에는 답글 아이콘 안 보여주니까 0
//        isReply = true,
//        prefix = "╰▷",
//        menuState = menuState,
//        onLikeClick = onLikeClick,
//        onReplyClick = onReplyClick,
//        onEditClick = { onEditClick() },
//        onDeleteClick = onDeleteClick,
//        onTimeToggle = onTimeToggle,
//        onMenuStateChange = onMenuStateChange,
//        formatRelativeTime = formatRelativeTime,
//        clearAllMenus = clearAllMenus,
//        clearCommentInput = clearCommentInput
//    )
//}
//
