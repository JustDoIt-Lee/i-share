package com.ryun.ishare.ui.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.ryun.ishare.viewmodel.IdeaListViewModel

fun Modifier.clearFocusOnTap(
    focusManager: FocusManager,
    viewModel: IdeaListViewModel,
    expandedCommentBoxId: String?
): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = {
        if (expandedCommentBoxId != null) return@detectTapGestures

        focusManager.clearFocus()
        viewModel.setExpandedDropdown(null)
        viewModel.setExpandedCommentBoxId(null)
        viewModel.setReplyTarget(null)
    })
}
