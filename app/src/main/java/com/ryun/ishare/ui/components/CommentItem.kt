//package com.ryun.ishare.ui.components
//
//import androidx.compose.runtime.Composable
//import com.ryun.ishare.ui.components.common.CommentReplyContent
//import com.ryun.ishare.ui.model.CommentUIModel
//import com.ryun.ishare.ui.state.CommentMenuState
//
//
//@Composable
//fun CommentItem(
//    model: CommentUIModel,
//    onLikeClick: (ideaId: String, index: Int, userId: String) -> Unit,
//    onReplyClick: (Pair<String, Int>?) -> Unit,
//    onEditClick: (String, Int, String) -> Unit,
//    onDeleteClick: (Int) -> Unit,
//    onTimeToggle: () -> Unit,
//    onMenuStateChange: (CommentMenuState) -> Unit,
//    clearAllMenus: () -> Unit,
//    clearCommentInput: (ideaId: String) -> Unit,
//    formatRelativeTime: (Long) -> String
//) {
//    CommentReplyContent(
//        ideaId = model.ideaId,
//        index = model.index,
//        nickname = model.nickname,
//        content = model.content,
//        userId = model.userId,
//        commentAuthorId = model.authorId,
//        timestamp = model.timestamp,
//        likeCount = model.likeCount,
//        replyCount = model.replyCount,
//        isReply = false,
//        prefix = null,
//        menuState = model.menuState,
//        onLikeClick = { onLikeClick(model.ideaId, model.index, model.userId) },
//        onReplyClick = { onReplyClick(model.ideaId to model.index) },
//        onEditClick = { onEditClick(model.ideaId, model.index, model.content) },
//        onDeleteClick = { onDeleteClick(model.index) },
//        onTimeToggle = onTimeToggle,
//        onMenuStateChange = onMenuStateChange,
//        formatRelativeTime = formatRelativeTime,
//        clearAllMenus = clearAllMenus,
//        clearCommentInput = { clearCommentInput(model.ideaId) }
//    )
//}
//
