package com.ryun.ishare.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ryun.ishare.R
import com.ryun.ishare.ui.components.IconWithOverlay
import com.ryun.ishare.ui.components.ShareDialogIfNeeded
import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.ui.state.ShareUIState
import com.ryun.ishare.ui.style.IdeaCardStyle
import com.ryun.ishare.viewmodel.IdeaListViewModel
import androidx.compose.ui.focus.FocusManager
import com.ryun.ishare.util.closeCommentBoxIfOpen
import com.ryun.ishare.util.toggleCommentBox

@Composable
fun IdeaCardIconGroup(
    ideaId: String,
    userId: String,
    viewModel: IdeaListViewModel,
    commentUIState: CommentUIState,
    shareUIState: ShareUIState,
    focusManager: FocusManager
) {
    val bulbCount = viewModel.bulbCounts[ideaId] ?: 0
    val shareCount = viewModel.shareCounts[ideaId] ?: 0
    val commentCount = viewModel.commentCounts[ideaId] ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = IdeaCardStyle.IconPaddingEnd),
        horizontalArrangement = Arrangement.spacedBy(IdeaCardStyle.IconSpacing, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 💡 전구
        IconWithOverlay(
            count = bulbCount,
            iconPainter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_originalbulb),
            onClick = {
                focusManager.clearFocus()
                closeCommentBoxIfOpen(ideaId, commentUIState, viewModel)
                viewModel.incrementBulbCount(ideaId, userId)
            }
        )

        // 📤 공유
        IconWithOverlay(
            count = shareCount,
            iconPainter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_originalshare),
            onClick = {
                focusManager.clearFocus()
                closeCommentBoxIfOpen(ideaId, commentUIState, viewModel)
                viewModel.setShareDialogIdeaId(ideaId)
            }
        )

        ShareDialogIfNeeded(ideaId, shareUIState, viewModel)

        // 💬 댓글
        IconWithOverlay(
            count = commentCount,
            iconPainter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_originalcomment),
            onClick = {
                focusManager.clearFocus()
                toggleCommentBox(ideaId, commentUIState, viewModel)
            }
        )
    }
}
