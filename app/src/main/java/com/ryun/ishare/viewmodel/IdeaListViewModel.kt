package com.ryun.ishare.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.ryun.ishare.data.repository.IdeaRepository
import com.ryun.ishare.model.Idea
import com.ryun.ishare.ui.model.CommentUIModel
import com.ryun.ishare.ui.state.CommentMenuState
import com.ryun.ishare.ui.state.CommentUIState
import com.ryun.ishare.ui.state.MenuType
import com.ryun.ishare.ui.state.ShareUIState
import com.ryun.ishare.util.UserManager
import com.ryun.ishare.util.filterIdeasByCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CategoryState(val category: String, val subCategory: String)

data class IdeaListViewState(
    val selectedCategory: String = "전체",
    val selectedSubCategory: String = "",
    val searchQuery: String = "",
    val selectedSortOption: String = "최신순",
    val expandedDropdown: String? = null,
    val sortExpanded: Boolean = false // ✅ 추가
)

data class CommentData(
    val text: String,
    val timestamp: Long,
    val authorId: String // ✅ 작성자 ID 추가
)

@HiltViewModel
class IdeaListViewModel @Inject constructor(
    private val repository: IdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeaListViewState())
    val uiState: StateFlow<IdeaListViewState> = _uiState
    val editingCommentIndexMap = mutableStateMapOf<String, Int?>()

    val bulbCounts = mutableStateMapOf<String, Int>()
    val shareCounts = mutableStateMapOf<String, Int>()
    val commentCounts = mutableStateMapOf<String, Int>()

    private val _commentMap = mutableStateMapOf<String, MutableList<CommentData>>()
    val commentMap: Map<String, List<CommentData>> get() = _commentMap

    //    val newCommentMap = mutableStateMapOf<String, String>()           // 댓글 입력값

    val userBulbMap = mutableStateMapOf<String, MutableSet<String>>() // ideaId -> userId Set
    val commentInputStateMap = mutableStateMapOf<String, MutableState<TextFieldValue>>()

    private val _sortExpanded = MutableStateFlow(false)
    val sortExpanded: StateFlow<Boolean> = _sortExpanded

    fun onSortDropdownToggle() {
        _sortExpanded.value = !_sortExpanded.value
    }

    fun deleteComment(ideaId: String, index: Int) {
        val comments = _commentMap[ideaId] ?: return
        if (index !in comments.indices) return

        val commentKey = "$ideaId-$index"

        // ✅ 1. 답글인지 아닌지 확인
        val isReply = comments[index].text.startsWith("↳") // ✅ text 기준으로 판별

        // ✅ 2. 답글이 아닌 기본 댓글이면 → 해당 댓글에 달린 답글도 함께 삭제
        if (!isReply) {
            val repliesToRemove = comments.withIndex().filter {
                it.value.text.startsWith("↳$index|")
            }

            // 역순으로 삭제 (인덱스 무너지는 거 방지)
            repliesToRemove.reversed().forEach { (replyIndex, _) ->
                comments.removeAt(replyIndex)

                // 답글 관련 카운트/상태도 정리 (선택 사항)
                val replyKey = "$ideaId-$replyIndex"
                _commentLikeCounts.remove(replyKey)
                commentLikeUsers.remove(replyKey)
                visibleRepliesMap.remove(ideaId to replyIndex)
            }

            // replyCounts도 제거
            _replyCounts.remove(commentKey)
        }

        // ✅ 3. 본 댓글 삭제
        comments.removeAt(index)
        _commentLikeCounts.remove(commentKey)
        commentLikeUsers.remove(commentKey)
        visibleRepliesMap.remove(ideaId to index)

        // ✅ 4. 댓글 수 갱신
        commentCounts[ideaId] = (commentCounts[ideaId] ?: 1) - 1
        // ✅ 5. 입력창 초기화 (커서 포함)
        commentInputStateMap[ideaId]?.value = TextFieldValue("", selection = TextRange(0))

// ✅ 6. 수정 상태 해제
        editingCommentIndexMap[ideaId] = null
    }

    fun addComment(ideaId: String, text: String) {
        val updated = commentMap[ideaId]?.toMutableList() ?: mutableListOf()
        updated.add(
            CommentData(
                text = text,
                timestamp = System.currentTimeMillis(),
                authorId = UserManager.currentUserId // ✅ 작성자 ID 추가
            )
        )
        _commentMap[ideaId] = updated
    }

    fun addReply(ideaId: String, parentIndex: Int, replyText: String) {
        val updated = commentMap[ideaId]?.toMutableList() ?: mutableListOf()
        val replyTextFormatted = "↳$parentIndex|$replyText"
        updated.add(
            CommentData(
                text = replyTextFormatted,
                timestamp = System.currentTimeMillis(),
                authorId = UserManager.currentUserId // ✅ 현재 유저 ID
            )
        )
        _commentMap[ideaId] = updated
    }

    fun onSortOptionSelect(option: String) {
        _uiState.update {
            it.copy(selectedSortOption = option)
        }
        _sortExpanded.value = false
        updateFilteredIdeas()
        updateMyFilteredIdeas() // ✅ 추가
    }

    fun setSortExpanded(expanded: Boolean) {
        _uiState.update { it.copy(sortExpanded = expanded) }
    }

    fun onReset(listState: LazyListState) {
        resetAll()
        viewModelScope.launch {
            listState.scrollToItem(0)
        }
    }

    // ViewModel 내에서 replyTarget을 관리
    private val _replyTarget = MutableStateFlow<Pair<String, Int>?>(null)
    val replyTarget: StateFlow<Pair<String, Int>?> = _replyTarget

    fun setReplyTarget(target: Pair<String, Int>?) {
        _replyTarget.update { current ->
            if (current == target) null else target
        }
    }

    val timeToggleMap = mutableStateMapOf<String, MutableState<MutableSet<Int>>>()
    fun toggleCommentTimeVisible(ideaId: String, index: Int) {
        val state = timeToggleMap.getOrPut(ideaId) { mutableStateOf(mutableSetOf()) }
        val currentSet = state.value.toMutableSet()
        if (currentSet.contains(index)) {
            currentSet.remove(index)
        } else {
            currentSet.add(index)
        }
        state.value = currentSet // ✅ 변경을 감지시킴
    }

    fun isCommentTimeVisible(ideaId: String, index: Int): Boolean {
        return timeToggleMap[ideaId]?.value?.contains(index) == true
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / 60000
        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            minutes < 1440 -> "${minutes / 60}시간 전"
            minutes < 10080 -> "${minutes / 1440}일 전"
            else -> SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(timestamp))
        }
    }


    private val _expandedCommentBoxId = MutableStateFlow<String?>(null)
    val expandedCommentBoxId: StateFlow<String?> = _expandedCommentBoxId

    // ✅ 공유 다이얼로그 상태 관리
    private val _shareDialogIdeaId = MutableStateFlow<String?>(null)
    val shareDialogIdeaId: StateFlow<String?> = _shareDialogIdeaId

    val isCommentScrolling = mutableStateOf(false)

    fun setCommentScrolling(scrolling: Boolean) {
        isCommentScrolling.value = scrolling
    }

    fun observePendingShareIntent(
        context: Context,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            snapshotFlow { shareUIState.value.pendingShareIdeaId }
                .collectLatest { id ->
                    id?.let {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "https://ishare.app/idea/$id")
                            type = "text/plain"
                        }
                        val chooser = Intent.createChooser(intent, "공유 방법 선택")
                        context.startActivity(chooser)
                        onComplete(id)
                    }
                }
        }
    }
    private val _filteredIdeas = MutableStateFlow<List<Idea>>(emptyList())
    val filteredIdeas: StateFlow<List<Idea>> = _filteredIdeas

    private val _myFilteredIdeas = MutableStateFlow<List<Idea>>(emptyList())
    val myFilteredIdeas: StateFlow<List<Idea>> = _myFilteredIdeas

    fun updateFilteredIdeas() {
        val state = uiState.value

        Log.d("💡updateFilteredIdeas", "📌 ideaList.size = ${ideaList.size}")
        Log.d("💡updateFilteredIdeas", "📌 selectedCategory = ${state.selectedCategory}, selectedSubCategory = ${state.selectedSubCategory}, searchQuery = '${state.searchQuery}', sort = ${state.selectedSortOption}")

        val searched = filterIdeasByCategory(
            ideas = ideaList.entries.toList(),
            selectedCategory = state.selectedCategory,
            selectedSubCategory = state.selectedSubCategory
        ).let { filtered ->
            if (state.searchQuery.isBlank()) filtered
            else filtered.filter {
                it.value.title.contains(state.searchQuery.trim(), ignoreCase = true)
            }
        }

        val sorted = when (state.selectedSortOption) {
            "전구순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { bulbCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            "공유순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { shareCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            "댓글순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { commentCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            else -> searched.sortedByDescending { it.value.timestamp }
        }

        _filteredIdeas.value = sorted.map { it.value }
    }

    private val _myIdeas = MutableStateFlow<List<Map.Entry<String, Idea>>>(emptyList())
    val myIdeas: StateFlow<List<Map.Entry<String, Idea>>> = _myIdeas

    fun updateMyIdeas(userId: String) {
        val all = ideaList.entries.toList()
        val mine = all.filter { it.value.userId == userId }
        _myIdeas.value = mine
        updateMyFilteredIdeas()
    }

    fun updateMyFilteredIdeas() {
        val state = uiState.value

        val searched = filterIdeasByCategory(
            ideas = ideaList.entries.filter { it.value.userId == UserManager.currentUserId },
            selectedCategory = state.selectedCategory,
            selectedSubCategory = state.selectedSubCategory
        ).let { filtered ->
            if (state.searchQuery.isBlank()) filtered
            else filtered.filter {
                it.value.title.contains(state.searchQuery.trim(), ignoreCase = true)
            }
        }

        val sorted = when (state.selectedSortOption) {
            "전구순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { bulbCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            "공유순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { shareCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            "댓글순" -> searched.sortedWith(
                compareByDescending<Map.Entry<String, Idea>> { commentCounts[it.key] ?: 0 }
                    .thenByDescending { it.value.timestamp }
            )
            else -> searched.sortedByDescending { it.value.timestamp }
        }

        _myFilteredIdeas.value = sorted.map { it.value }
    }

    private var isFromDetailHandled = false

    fun handleNavigationArgumentsFrom(navBackStackEntry: NavBackStackEntry?) {
        if (isFromDetailHandled) return

        val currentCategoryParam = navBackStackEntry?.arguments?.getString("category")
        val currentSubCategoryParam = navBackStackEntry?.arguments?.getString("subCategory")

        if (!currentCategoryParam.isNullOrEmpty()) {
            selectCategory(currentCategoryParam)
        }
        if (!currentSubCategoryParam.isNullOrEmpty()) {
            selectSubCategory(currentSubCategoryParam)
        }

        isFromDetailHandled = true
    }
    fun observeScrollToCollapseUI(
        listState: LazyListState,
        focusManager: FocusManager,
        onDropdownClose: () -> Unit
    ) {
        viewModelScope.launch {
            snapshotFlow { listState.isScrollInProgress }
                .collect { isScrolling ->
                    if (isScrolling) {
                        focusManager.clearFocus()
                        setSortExpanded(false)
                        onDropdownClose()
                    }
                }
        }
    }

    // 2. Key 생성 유틸 함수
    private fun makeMenuKey(id: String, index: Int, type: MenuType): String =
        "$id-$index-${type.name.lowercase()}"

    // 3. 상태 관리 Map (변경 없음)
    private val menuStateMap = mutableStateMapOf<String, CommentMenuState>()

    // 4. getter/setter 함수 통일
    fun getMenuState(id: String, index: Int, type: MenuType): CommentMenuState {
        return menuStateMap[makeMenuKey(id, index, type)] ?: CommentMenuState.Collapsed
    }

    fun setMenuState(id: String, index: Int, type: MenuType, state: CommentMenuState) {
        menuStateMap[makeMenuKey(id, index, type)] = state
    }

    fun clearAllMenus() {
        menuStateMap.clear()
    }

    fun setExpandedDropdown(value: String?) {
        _uiState.update { it.copy(expandedDropdown = value) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredIdeas()
        updateMyFilteredIdeas() // ✅ 추가
    }

    fun getMutableCommentMap(): MutableMap<String, MutableList<CommentData>> = _commentMap

    // ✅ 기존 멤버 바꾸기
    private val _shareUIState = MutableStateFlow(ShareUIState())
    val shareUIState: StateFlow<ShareUIState> = _shareUIState

    fun setShareDialogIdeaId(id: String?) {
        _shareUIState.update { it.copy(shareDialogIdeaId = id) }
    }

    fun setPendingShareIdeaId(id: String?) {
        _shareUIState.update { it.copy(pendingShareIdeaId = id) }
    }

    private val _pendingShareIdeaId = MutableStateFlow<String?>(null)
    val pendingShareIdeaId: StateFlow<String?> = _pendingShareIdeaId

    fun setExpandedCommentBoxId(id: String?) {
        _expandedCommentBoxId.value = id // ✅ 이걸로만 관리
    }
    // 추가
    // 🔽 ideaList를 Repository에서 받아오는 방식으로 추가
    val ideaList: Map<String, Idea> get() = repository.getAllIdeas().associateBy { it.id }

    val commentLikeUsers = mutableStateMapOf<String, MutableSet<String>>() // ✅ MutableSet으로 수정
    private val _commentLikeCounts = mutableStateMapOf<String, Int>()
    val commentLikeCounts: Map<String, Int> get() = _commentLikeCounts


    private val _replyCounts = mutableStateMapOf<String, Int>()
    val replyCounts: Map<String, Int> get() = _replyCounts

    val visibleRepliesMap = mutableStateMapOf<Pair<String, Int>, Boolean>() // 💬 답글 펼침 여부
    val isFocusedMap = mutableStateMapOf<String, Boolean>()          // 💬 댓글창 포커스 여부

    fun initReplyCountMapFrom(commentMap: Map<String, List<CommentData>>) {
        commentMap.forEach { (ideaId, comments) ->
            comments.forEachIndexed { index, commentData ->
                val comment = commentData.text
                if (comment.startsWith("↳")) {
                    val parentIndex = comment.removePrefix("↳").substringBefore("|").toIntOrNull()
                    if (parentIndex != null) {
                        val key = "$ideaId-$parentIndex"
                        _replyCounts[key] = (replyCounts[key] ?: 0) + 1
                    }
                }
            }
        }
    }

    fun getCommentUIState(): CommentUIState {
        return CommentUIState(
            expandedCommentBoxId = expandedCommentBoxId, // ✅ Flow 전달
            replyTarget = replyTarget, // ✅ 여기 StateFlow 자체를 넘김
            commentMap = _commentMap, // ✅ 여기! 이미 멤버 변수
            commentInputStateMap = commentInputStateMap, // ✅ 이 줄 추가
            commentCounts = commentCounts,
            commentLikeUsers = commentLikeUsers,
            commentLikeCounts = _commentLikeCounts,
            replyCounts = _replyCounts,
            visibleRepliesMap = visibleRepliesMap,
            isFocusedMap = isFocusedMap,
            onReplyTargetChange = { setReplyTarget(it) },
            onExpandedCommentBoxIdChange = { setExpandedCommentBoxId(it) },
            onExpandedDropdownChange = { setExpandedDropdown(it) },
            menuStateMap = menuStateMap
        )
    }

    fun incrementBulbCount(ideaId: String, userId: String) {
        val userSet = userBulbMap.getOrPut(ideaId) { mutableSetOf() }

        if (userSet.add(userId)) {
            // 처음 누른 사용자만 +1 증가
            bulbCounts[ideaId] = (bulbCounts[ideaId] ?: 0) + 1
        }
    }

    fun incrementShareCount(ideaId: String) {
        shareCounts[ideaId] = (shareCounts[ideaId] ?: 0) + 1
    }

    fun incrementCommentCount(ideaId: String) {
        commentCounts[ideaId] = (commentCounts[ideaId] ?: 0) + 1
    }

//    fun onCommentInputChanged(ideaId: String, newText: String) {
//        newCommentMap[ideaId] = newText
//    }
    fun cancelEditing(ideaId: String) {
        editingCommentIndexMap[ideaId] = null
        commentInputStateMap[ideaId]?.value = TextFieldValue("") // 선택: 입력값도 초기화
    }

    fun clearVisibleReplies(ideaId: String) {
        visibleRepliesMap.keys
            .filter { it.first == ideaId }
            .forEach { visibleRepliesMap[it] = false }
    }

    private var isInitialized = false

    fun initializeScreen(
        context: Context,
        navBackStackEntry: NavBackStackEntry?,
        onShare: (ideaId: String) -> Unit
    ) {
        if (isInitialized) return
        isInitialized = true

        updateFilteredIdeas()
        handleNavigationArgumentsFrom(navBackStackEntry)

        // 공유 인텐트 처리
        viewModelScope.launch {
            observePendingShareIntent(
                context = context,
                onComplete = onShare
            )
        }

        initReplyCountMapFrom(commentMap)
    }

    fun submitComment(
        ideaId: String,
        isReplying: Boolean,
        replyTarget: Pair<String, Int>?,
        focusManager: FocusManager,
    ) {
        val comment = commentInputStateMap[ideaId]?.value?.text?.trim()
        val editIndex = editingCommentIndexMap[ideaId]

        if (!comment.isNullOrEmpty()) {
            val comments = _commentMap.getOrPut(ideaId) { mutableListOf() }

            when {
                editIndex != null && editIndex in comments.indices -> {
                    comments[editIndex] = CommentData(
                        text = comment,
                        timestamp = System.currentTimeMillis(),
                        authorId = UserManager.currentUserId
                    )
                    editingCommentIndexMap[ideaId] = null
                }

                isReplying && replyTarget?.first == ideaId -> {
                    val parentIndex = replyTarget.second
                    val replyTextFormatted = "↳$parentIndex|$comment"
                    comments.add(
                        CommentData(
                            text = replyTextFormatted,
                            timestamp = System.currentTimeMillis(),
                            authorId = UserManager.currentUserId
                        )
                    )
                    val replyKey = "$ideaId-$parentIndex"
                    _replyCounts[replyKey] = (_replyCounts[replyKey] ?: 0) + 1
                    visibleRepliesMap[ideaId to parentIndex] = true
//                    setReplyTarget(ideaId to parentIndex)
                }

                else -> {
                    addComment(ideaId, comment) // ✅ 여기만 교체
                    incrementCommentCount(ideaId)
                }
            }

            commentInputStateMap[ideaId]?.value = TextFieldValue("")
            focusManager.clearFocus()
        }
    }

    fun isReplying(ideaId: String, expandedId: String?): Boolean {
        val target = replyTarget.value
        return target?.first == ideaId && expandedId == ideaId
    }

    fun getCommentInputState(ideaId: String): MutableState<TextFieldValue> {
        return commentInputStateMap.getOrPut(ideaId) {
            mutableStateOf(TextFieldValue())
        }
    }

    private val _history = MutableStateFlow<List<CategoryState>>(emptyList())

    val history: StateFlow<List<CategoryState>> = _history


    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedSubCategory = ""
            )
        }
        updateFilteredIdeas()
        updateMyFilteredIdeas() // ✅ 추가
    }

    fun selectSubCategory(subCategory: String) {
        _uiState.update {
            it.copy(selectedSubCategory = if (subCategory == "전체") "" else subCategory)
        }
        updateFilteredIdeas()
        updateMyFilteredIdeas() // ✅ 추가
    }

    fun resetAll() {
        _uiState.update {
            it.copy(
                selectedCategory = "전체",
                selectedSubCategory = "",
                searchQuery = "",
                selectedSortOption = "최신순",
                expandedDropdown = null
            )
        }

        _expandedCommentBoxId.value = null // ✅ 이걸 직접 초기화
        _replyTarget.value = null
        _history.value = emptyList()

        updateFilteredIdeas()

        Log.d("💡ViewModel", "🔄 전체 상태 초기화")
    }

    fun addHistory(category: String, subCategory: String) {

        _history.update { it + CategoryState(category, subCategory) }
        Log.d("💡ViewModel", "✅ addHistory 호출됨: $category / $subCategory")
    }

    fun handleCommentLikeClick(ideaId: String, index: Int, userId: String) {
        val key = "$ideaId-$index"
        val users = commentLikeUsers.getOrPut(key) { mutableSetOf() }

        if (!users.contains(userId)) {
            users.add(userId)
            _commentLikeCounts[key] = (commentLikeCounts[key] ?: 0) + 1
        }
        // 이미 누른 경우 아무 동작 안 함
    }

    fun startEditing(ideaId: String, index: Int, content: String, focusRequester: FocusRequester) {
        editingCommentIndexMap[ideaId] = index
        commentInputStateMap[ideaId]?.value = TextFieldValue(content)
        focusRequester.requestFocus()
    }


    fun clearInputTextOnly(ideaId: String) {
        commentInputStateMap[ideaId]?.value = TextFieldValue("")
    }

    fun clearInputAndFocus(ideaId: String, focusManager: FocusManager) {
        commentInputStateMap[ideaId]?.value = TextFieldValue("")
        focusManager.clearFocus()
    }

    fun collapseMenuForComment(ideaId: String, index: Int) {
        setMenuState(ideaId, index, MenuType.COMMENT, CommentMenuState.Collapsed)
    }

    fun goBack() {
        val currentHistory = _history.value
        if (currentHistory.isNotEmpty()) {
            val previousState = currentHistory.last()
            _history.value = currentHistory.dropLast(1)

            println("⬅️ Going Back to: category=${previousState.category}, subCategory=${previousState.subCategory}")
            Log.d("💡ViewModel", "⬅️ GoBack: ${previousState.category} / ${previousState.subCategory}")
            Log.d("💡ViewModel", "📜 History after drop: $_history")

            // ✅ UI 상태를 강제로 갱신
            _uiState.update {
                it.copy(
                    selectedCategory = previousState.category,
                    selectedSubCategory = previousState.subCategory
                )
            }
            updateFilteredIdeas()
        } else {
            println("⛔️ History is empty. Reset to default")
            Log.d("💡ViewModel", "⛔️ History empty → 초기화")

            _uiState.update {
                it.copy(
                    selectedCategory = "전체",
                    selectedSubCategory = ""
                )
            }
            updateFilteredIdeas()
        }
    }
    private val defaultModel = CommentUIModel(
        ideaId = "",
        index = -1,
        content = "",
        nickname = "알 수 없음",
        userId = "",
        authorId = "",
        timestamp = 0L,
        likeCount = 0,
        replyCount = 0,
        menuState = CommentMenuState.Collapsed, // ✅ 이걸로 설정
        menuType = MenuType.COMMENT // ✅ 반드시 추가!
    )
    val userNicknameMap = mapOf(
        "user123" to "강륜",
        "user456" to "철수"
        // ...
    )

    private fun getNickname(userId: String): String {
        return userNicknameMap[userId] ?: "알 수 없음"
    }
    fun getCommentUIModel(ideaId: String, index: Int): CommentUIModel {
        val commentData = commentMap[ideaId]?.getOrNull(index) ?: return defaultModel
        val key = "$ideaId-$index"

        val isReply = commentData.text.startsWith("↳")
        val prefix = if (isReply) "╰▷" else null
        val content = if (isReply) {
            commentData.text.substringAfter("|") // ↳3|내용 → 내용만 추출
        } else {
            commentData.text
        }
        val replyCount = if (isReply) 0 else replyCounts[key] ?: 0
        val menuType = if (isReply) MenuType.REPLY else MenuType.COMMENT

        return CommentUIModel(
            ideaId = ideaId,
            index = index,
            content = content,
            nickname = getNickname(commentData.authorId),
            userId = UserManager.currentUserId,
            authorId = commentData.authorId,
            timestamp = commentData.timestamp,
            likeCount = commentLikeCounts[key] ?: 0,
            replyCount = replyCount,
            isReply = isReply,
            prefix = prefix,
            menuState = getMenuState(ideaId, index, menuType),
            menuType = menuType
        )
    }

    fun getAllCommentUIModels(ideaId: String): List<CommentUIModel> {
        return commentMap[ideaId]?.mapIndexed { index, _ ->
            getCommentUIModel(ideaId, index)
        } ?: emptyList()
    }

    fun clearHistory() {

        _history.value = emptyList()

    }

}