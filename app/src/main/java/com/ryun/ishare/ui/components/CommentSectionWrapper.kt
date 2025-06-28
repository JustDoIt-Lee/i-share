package com.ryun.ishare.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusManager
import com.ryun.ishare.viewmodel.IdeaListViewModel
import com.ryun.ishare.ui.state.CommentUIState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun CommentSectionWrapper(
    ideaId: String,
    userId: String,
    viewModel: IdeaListViewModel,
    commentUIState: CommentUIState,
    focusManager: FocusManager
) {
    val expandedCommentBoxId by commentUIState.expandedCommentBoxId.collectAsStateWithLifecycle()

    if (expandedCommentBoxId == ideaId) {
        LaunchedEffect(ideaId) {
            viewModel.clearAllMenus()
        }

        CommentSection(
            ideaId = ideaId,
            userId = userId,
            viewModel = viewModel,

            commentUIState = commentUIState,
            expandedCommentBoxId = expandedCommentBoxId,
            visibleRepliesMap = commentUIState.visibleRepliesMap,

            onExpandedCommentBoxIdChange = commentUIState.onExpandedCommentBoxIdChange,
            onExpandedDropdownChange = commentUIState.onExpandedDropdownChange,

            focusManager = focusManager,
        )
    }
}
