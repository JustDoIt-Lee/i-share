package com.ryun.ishare.ui.components.effect

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.ryun.ishare.viewmodel.IdeaListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HandleCommentScroll(
    scrollState: ScrollState,
    viewModel: IdeaListViewModel
) {
    LaunchedEffect(key1 = scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                viewModel.setCommentScrolling(isScrolling)
                if (isScrolling) viewModel.clearAllMenus()
            }
    }
}
