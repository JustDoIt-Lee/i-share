package com.ryun.ishare.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.ryun.ishare.viewmodel.IdeaListViewModel

import androidx.compose.ui.focus.FocusManager
import com.ryun.ishare.ui.components.common.IdeaCardIconGroup
import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.ui.state.ShareUIState
import com.ryun.ishare.ui.style.IdeaCardStyle
import com.ryun.ishare.util.truncateTitle

@Composable
fun IdeaCard(
    ideaId: String,
    title: String,
    author: String,
    index : Int,
    category: String,
    navController: NavController,
    viewModel: IdeaListViewModel,
    focusManager: FocusManager,
    userId: String,
    commentUIState: CommentUIState,
    shareUIState: ShareUIState
) {
    val displayedTitle = truncateTitle(title)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(IdeaCardStyle.CardHeight)
                .clickable { navController.navigate("idea_detail/$ideaId") },
            shape = RoundedCornerShape((IdeaCardStyle.CornerRadius)),
            color = Color.White,
            tonalElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(IdeaCardStyle.CategoryBoxPadding),
                contentAlignment = Alignment.TopStart
            ) {
                Text(text = category, style = IdeaCardStyle.CategoryTextStyle)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(IdeaCardStyle.AuthorBoxPadding),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(text = author, style = IdeaCardStyle.AuthorTextStyle)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(IdeaCardStyle.TitleRowPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = displayedTitle, style = IdeaCardStyle.TitleTextStyle)
            }
        }

        Spacer(modifier = Modifier.height(IdeaCardStyle.SpacerBetweenCardAndIcons))

        IdeaCardIconGroup(
            ideaId = ideaId,
            userId = userId,
            viewModel = viewModel,
            commentUIState = commentUIState,
            shareUIState = shareUIState,
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(IdeaCardStyle.SpacerBelowIcons))

        CommentSectionWrapper(
            ideaId = ideaId,
            userId = userId,
            viewModel = viewModel,
            commentUIState = commentUIState,
            focusManager = focusManager
        )
    }
}

