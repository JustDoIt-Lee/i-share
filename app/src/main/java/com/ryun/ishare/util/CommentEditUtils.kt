package com.ryun.ishare.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange // 👈 수정된 부분
import com.ryun.ishare.viewmodel.IdeaListViewModel

fun onEditClick(
    ideaId: String,
    index: Int,
    comment: String,
    commentInputStateMap: MutableMap<String, MutableState<TextFieldValue>>,
    editingCommentIndexMap: MutableMap<String, Int?>, // ✅ 고쳤음!
) {
    val newValue = TextFieldValue(
        text = comment,
        selection = TextRange(comment.length)
    )
    commentInputStateMap.getOrPut(ideaId) { mutableStateOf(TextFieldValue()) }.value = newValue
    editingCommentIndexMap[ideaId] = index
}