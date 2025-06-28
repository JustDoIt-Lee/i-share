package com.ryun.ishare.ui.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusManager
import com.ryun.ishare.viewmodel.IdeaListViewModel

@Composable
fun HandleScrollEffect(
    listState: LazyListState,
    viewModel: IdeaListViewModel,
    focusManager: FocusManager
) {
    LaunchedEffect(listState) {
        var lastIndex = listState.firstVisibleItemIndex
        var lastOffset = listState.firstVisibleItemScrollOffset

        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val isUserScrolled = index != lastIndex || offset != lastOffset
                lastIndex = index
                lastOffset = offset

                if (isUserScrolled) {
                    focusManager.clearFocus()

                    if (!viewModel.isCommentScrolling.value) {
                        viewModel.setExpandedDropdown(null)
                        viewModel.setExpandedCommentBoxId(null)
                        viewModel.setReplyTarget(null)
                    }
                }
            }
    }
}
