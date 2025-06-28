package com.ryun.ishare.util

import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.viewmodel.IdeaListViewModel

fun closeCommentBoxIfOpen(
    ideaId: String,
    commentUIState: CommentUIState,
    viewModel: IdeaListViewModel
) {
    val expandedId = commentUIState.expandedCommentBoxId.value
    if (expandedId != null && expandedId != ideaId) {
        viewModel.setExpandedCommentBoxId(null)
        viewModel.setReplyTarget(null)
        viewModel.cancelEditing(expandedId)
        viewModel.clearVisibleReplies(expandedId)
        viewModel.clearInputTextOnly(expandedId)
    }
}

fun toggleCommentBox(
    ideaId: String,
    commentUIState: CommentUIState,
    viewModel: IdeaListViewModel
) {
    viewModel.setReplyTarget(null)
    viewModel.cancelEditing(ideaId)
    viewModel.clearAllMenus()
    viewModel.clearInputTextOnly(ideaId)
    viewModel.clearVisibleReplies(ideaId)

    val isAlreadyOpen = commentUIState.expandedCommentBoxId.value == ideaId
    viewModel.setExpandedCommentBoxId(if (isAlreadyOpen) null else ideaId)
}
