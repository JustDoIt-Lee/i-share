package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.ui.components.common.CommentReplyContent
import com.ryun.ishare.ui.extensions.isReply
import com.ryun.ishare.ui.extensions.isReplyVisible
import com.ryun.ishare.ui.extensions.replyToIndex
import com.ryun.ishare.ui.model.CommentUIModel
import com.ryun.ishare.ui.state.MenuType
import com.ryun.ishare.viewmodel.IdeaListViewModel
import com.ryun.ishare.R

@Composable
fun CommentList(
    ideaId: String,
    userId: String,
    viewModel: IdeaListViewModel,
    focusRequester: FocusRequester,
) {
    val uiState = viewModel.getCommentUIState()
    val comments = uiState.commentMap[ideaId] ?: emptyList()

    Column(modifier = Modifier.fillMaxWidth()) {

        if (comments.none { !it.isReply() }) {
            Text(
                text = stringResource(R.string.no_comments),
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        comments.forEachIndexed { idx, comment ->
            if (!comment.isReply()) {
                val commentModel = viewModel.getCommentUIModel(ideaId, idx)

                key("$ideaId-$idx") {
                    CommentReplyComposable(
                        ideaId = ideaId,
                        index = idx,
                        model = commentModel,
                        userId = userId,
                        viewModel = viewModel,
                        menuType = MenuType.COMMENT,
                        onReplyClick = { viewModel.setReplyTarget(ideaId to idx) },
                        focusRequester = focusRequester
                    )
                }

                val replies = comments.withIndex().filter {
                    it.value.replyToIndex() == idx
                }

                val isReplyVisible = uiState.visibleRepliesMap.isReplyVisible(ideaId, idx)

                if (isReplyVisible) {
                    replies.forEachIndexed { i, (replyIndex, _) ->
                        val replyModel = viewModel.getCommentUIModel(ideaId, replyIndex)

                        key("$ideaId-reply-$replyIndex") {
                            CommentReplyComposable(
                                ideaId = ideaId,
                                index = replyIndex,
                                model = replyModel,
                                userId = userId,
                                viewModel = viewModel,
                                menuType = MenuType.REPLY,
                                onReplyClick = null,
                                focusRequester = focusRequester
                            )
                        }

                        if (i != replies.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if ((uiState.replyCounts["$ideaId-$idx"] ?: 0) > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentReplyComposable(
    ideaId: String,
    index: Int,
    model: CommentUIModel,
    userId: String,
    viewModel: IdeaListViewModel,
    menuType: MenuType,
    onReplyClick: (() -> Unit)?,
    focusRequester: FocusRequester
) {
    CommentReplyContent(
        model = model,
        currentUserId = userId,
        onLikeClick = { viewModel.handleCommentLikeClick(ideaId, index, userId) },
        onReplyClick = onReplyClick,
        onEditClick = {
            viewModel.startEditing(ideaId, index, model.content, focusRequester)
        },
        onDeleteClick = { viewModel.deleteComment(ideaId, index) },
        onTimeToggle = { viewModel.toggleCommentTimeVisible(ideaId, index) },
        onMenuStateChange = { newState ->
            viewModel.clearAllMenus()
            viewModel.setMenuState(ideaId, index, menuType, newState)
        },
        clearAllMenus = viewModel::clearAllMenus,
        clearInputTextOnly = { viewModel.clearInputTextOnly(ideaId) },
        formatRelativeTime = viewModel::formatRelativeTime
    )
}
