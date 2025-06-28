package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.viewmodel.IdeaListViewModel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.ryun.ishare.R
import com.ryun.ishare.ui.components.effect.HandleCommentScroll
import com.ryun.ishare.ui.components.handler.handleEditCancel
import com.ryun.ishare.ui.components.handler.handleReplyCancel
import com.ryun.ishare.ui.state.CommentInputUIState
import com.ryun.ishare.ui.state.CommentMenuState
import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.ui.state.MenuType
import com.ryun.ishare.ui.theme.Dimens.CommentSectionMaxHeight
import com.ryun.ishare.util.rememberScrollStateWithKey

@Composable
fun CommentSection(
    ideaId: String,
    userId: String,
    viewModel: IdeaListViewModel,
    expandedCommentBoxId: String?,
    onExpandedCommentBoxIdChange: (String?) -> Unit,
    visibleRepliesMap: MutableMap<Pair<String, Int>, Boolean>,
    onExpandedDropdownChange: (String?) -> Unit,
    focusManager: FocusManager,
    commentUIState: CommentUIState,
) {
    val replyTarget by viewModel.replyTarget.collectAsState()
    val isReplying by remember(expandedCommentBoxId, replyTarget) {
        derivedStateOf { viewModel.isReplying(ideaId, expandedCommentBoxId) }
    }
    val focusRequester = remember { FocusRequester() }

    val showReplyNotice = remember { mutableStateOf(false) }

    val replyIdeaId = replyTarget?.first
    val isFocusedOnReply = replyIdeaId?.let { commentUIState.isFocusedMap[it] == true } == true
    val isDifferentFromExpanded = replyIdeaId != null && replyIdeaId != expandedCommentBoxId

    LaunchedEffect(key1 = replyIdeaId, key2 = isFocusedOnReply) {
        showReplyNotice.value = replyIdeaId != null && isFocusedOnReply
    }

    LaunchedEffect(key1 = replyIdeaId, key2 = expandedCommentBoxId) {
        if (isDifferentFromExpanded) {
            onExpandedCommentBoxIdChange(replyIdeaId)
        }
    }

    val commentInputState = viewModel.getCommentInputState(ideaId)

    val commentScrollState = rememberScrollStateWithKey(ideaId)

    HandleCommentScroll(scrollState = commentScrollState, viewModel = viewModel)

    val isEditing by remember(viewModel.editingCommentIndexMap, ideaId) {
        derivedStateOf {
            viewModel.editingCommentIndexMap[ideaId] != null
        }
    }

    val commentInputUIState = CommentInputUIState(
        isReplying = isReplying,
        isEditing = isEditing,
        isFocused = commentUIState.isFocusedMap[ideaId] == true
    )

    val placeholderText = if (isReplying)
        stringResource(R.string.reply_placeholder)
    else
        stringResource(R.string.comment_placeholder)

    ShowReplyNoticeIfNeeded(
        ideaId = ideaId,
        showReplyNotice = showReplyNotice.value,
        replyTarget = replyTarget,
        viewModel = viewModel
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.6f),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = CommentSectionMaxHeight)
                    .verticalScroll(commentScrollState)
            ) {
                CommentList(
                    ideaId = ideaId,
                    userId = userId,
                    viewModel = viewModel,
                    focusRequester = focusRequester,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            CommentInputRow(
                ideaId = ideaId,
                inputState = commentInputUIState,
                commentInputState = commentInputState,
                onConfirmClick = {
                    val editingIndex = viewModel.editingCommentIndexMap[ideaId]

                    viewModel.submitComment(
                        ideaId = ideaId,
                        isReplying = isReplying,
                        replyTarget = replyTarget,
                        focusManager = focusManager
                    )
                    if (editingIndex != null) {
                        viewModel.setMenuState(ideaId, editingIndex, MenuType.COMMENT, CommentMenuState.Collapsed)
                    }

                    viewModel.clearInputAndFocus(ideaId, focusManager)
                },
                onCancelClick = {
                    when {
                        replyTarget?.first == ideaId -> {
                            handleReplyCancel(
                                ideaId = ideaId,
                                replyTarget = replyTarget,
                                visibleRepliesMap = visibleRepliesMap,
                                viewModel = viewModel
                            )
                        }

                        viewModel.editingCommentIndexMap[ideaId] != null -> {
                            handleEditCancel(
                                ideaId = ideaId,
                                viewModel = viewModel,
                                focusManager = focusManager
                            )
                        }

                        else -> {
                            onExpandedCommentBoxIdChange(null)
                            viewModel.clearAllMenus()
                        }
                    }

                    viewModel.clearInputAndFocus(ideaId, focusManager)
                },
                onFocusChanged = { isFocused ->
                    commentUIState.isFocusedMap[ideaId] = isFocused
                    if (isFocused) onExpandedDropdownChange(null)
                },
                placeholder = placeholderText,
                focusRequester = focusRequester
            )
        }
    }
}

@Composable
private fun ReplyNotice(text: String) {
    Text(
        text = "\uD83D\uDD25 '$text' 에 대한 답글 작성 중",
        fontSize = 11.sp,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun ShowReplyNoticeIfNeeded(
    ideaId: String,
    showReplyNotice: Boolean,
    replyTarget: Pair<String, Int>?,
    viewModel: IdeaListViewModel
) {
    if (showReplyNotice && replyTarget?.first == ideaId) {
        val index = replyTarget.second
        val parentComment = viewModel.commentMap[ideaId]?.getOrNull(index)?.text
        if (parentComment != null) {
            ReplyNotice(text = parentComment)
        }
    }
}
