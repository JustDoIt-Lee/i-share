package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ryun.ishare.model.Idea
import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.ui.state.ShareUIState
import com.ryun.ishare.util.UserManager
import com.ryun.ishare.viewmodel.IdeaListViewModel

@Composable
fun IdeaListContent(
    ideas: List<Idea>,
    listState: LazyListState,
    bottomPadding: Dp,
    navController: NavController,
    viewModel: IdeaListViewModel,
    focusManager: FocusManager,
    userId: String,
    commentUIState: CommentUIState,
    shareUIState: ShareUIState
) {
    LaunchedEffect(ideas) {
        println("💡 IdeaListContent ideas.size = ${ideas.size}")
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = bottomPadding)
    ) {
        itemsIndexed(ideas, key = { _, idea -> idea.id }) { index, idea ->
            IdeaCard(
                ideaId = idea.id,
                title = idea.title,
                author = UserManager.getNickname(idea.userId),
                index = index,
                category = idea.category,
                navController = navController,
                viewModel = viewModel,
                focusManager = focusManager,
                userId = userId,
                commentUIState = commentUIState,
                shareUIState = shareUIState
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
