package com.ryun.ishare.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import com.ryun.ishare.viewmodel.CommentData
import kotlinx.coroutines.flow.StateFlow

data class CommentUIState(
    val expandedCommentBoxId: StateFlow<String?>, // ✅ 변경!
    val onExpandedCommentBoxIdChange: (String?) -> Unit,
    val replyTarget: StateFlow<Pair<String, Int>?>, // ✅ 여기 중요
    val onReplyTargetChange: (Pair<String, Int>?) -> Unit,
    val commentMap: MutableMap<String, MutableList<CommentData>>, // ✅ 여기 수정
    val commentCounts: Map<String, Int>, // ✅ 요거 추가
    val commentLikeUsers: MutableMap<String, MutableSet<String>>,
    val commentLikeCounts: MutableMap<String, Int>,
    val replyCounts: MutableMap<String, Int>,
    val visibleRepliesMap: MutableMap<Pair<String, Int>, Boolean>,
    val isFocusedMap: MutableMap<String, Boolean>,
    val commentInputStateMap: MutableMap<String, MutableState<TextFieldValue>>,
    val onExpandedDropdownChange: (String?) -> Unit, // 추가
    val menuStateMap: MutableMap<String, CommentMenuState>
)

// ✅ 헬퍼 함수 추가
fun CommentUIState.isMenuExpanded(
    ideaId: String,
    index: Int,
    type: String = "comment"
): Boolean {
    val key = "$ideaId-$index-$type"
    return menuStateMap[key] is CommentMenuState.Expanded
}