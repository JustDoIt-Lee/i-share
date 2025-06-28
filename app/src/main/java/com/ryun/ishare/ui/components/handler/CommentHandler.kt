package com.ryun.ishare.ui.components.handler

import androidx.compose.ui.focus.FocusManager
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.focus.FocusRequester
//import com.ryun.ishare.util.onEditClick
import com.ryun.ishare.viewmodel.IdeaListViewModel

fun handleReplyCancel(
    ideaId: String,
    replyTarget: Pair<String, Int>?,
    visibleRepliesMap: MutableMap<Pair<String, Int>, Boolean>,
    viewModel: IdeaListViewModel
) {
    if (replyTarget?.first == ideaId) {
        val (targetIdeaId, targetIndex) = replyTarget
        visibleRepliesMap[targetIdeaId to targetIndex] = false
        viewModel.setReplyTarget(null)
        viewModel.collapseMenuForComment(targetIdeaId, targetIndex)
    }
}

fun handleEditCancel(
    ideaId: String,
    viewModel: IdeaListViewModel,
    focusManager: FocusManager
) {
    viewModel.editingCommentIndexMap[ideaId]?.let { editingIndex ->
        viewModel.cancelEditing(ideaId)
        focusManager.clearFocus()
        viewModel.collapseMenuForComment(ideaId, editingIndex)
    }
}

//fun commentReplyClick(
//    ideaId: String,
//    index: Int,
//    viewModel: IdeaListViewModel,
//    visibleRepliesMap: MutableMap<Pair<String, Int>, Boolean>,
//    onReplyClick: (Pair<String, Int>?) -> Unit,
//    focusManager: FocusManager
//) {
//    val key = ideaId to index
//    val isVisible = visibleRepliesMap[key] ?: false
//
//    visibleRepliesMap.keys.forEach { visibleRepliesMap[it] = false }
//    visibleRepliesMap[key] = !isVisible
//
//    onReplyClick(if (!isVisible) key else null)
//
//    viewModel.editingCommentIndexMap.remove(ideaId)
//
//    val replyKey = "$ideaId-$index"
//    viewModel.commentInputStateMap.getOrPut(replyKey) {
//        mutableStateOf(TextFieldValue())
//    }.value = TextFieldValue()
//
//    focusManager.clearFocus()
//}
//
//fun handleEditClick(
//    ideaId: String,
//    index: Int,
//    comment: String,
//    viewModel: IdeaListViewModel,
//    focusRequester: FocusRequester
//) {
//    viewModel.setReplyTarget(null)
//
//    // 기존의 유틸 함수 재사용
//    onEditClick(
//        ideaId = ideaId,
//        index = index,
//        comment = comment,
//        commentInputStateMap = viewModel.commentInputStateMap,
//        editingCommentIndexMap = viewModel.editingCommentIndexMap
//    )
//
//    focusRequester.requestFocus()
//}
//
//fun replyClick(
//    ideaId: String,
//    index: Int,
//    viewModel: IdeaListViewModel,
//    focusManager: FocusManager
//) {
//    val target = ideaId to index
//    val commentKey = "$ideaId-$index"
//
//    viewModel.setReplyTarget(target)
//    viewModel.editingCommentIndexMap.remove(ideaId)
//    viewModel.commentInputStateMap.getOrPut(commentKey) {
//        mutableStateOf(TextFieldValue())
//    }.value = TextFieldValue()
//
//    focusManager.clearFocus()
//}

