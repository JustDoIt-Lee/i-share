/*
package com.ryun.ishare.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.R
import com.ryun.ishare.ui.theme.shizuruFont // shizuruFont 가져오기
import androidx.navigation.NavController // 수정된 부분: NavController import 추가
//import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.text.style.TextAlign
//import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.ryun.ishare.viewmodel.IdeaListViewModel
import com.ryun.ishare.ui.data.myDummyDatas
import com.ryun.ishare.model.Idea
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//import com.ryun.ishare.data.bulbCounts
//import com.ryun.ishare.data.shareCounts
//import com.ryun.ishare.data.commentCounts
import com.ryun.ishare.ui.data.commentMap
import com.ryun.ishare.ui.data.dummyDatas
import com.ryun.ishare.ui.data.newCommentMap
import com.ryun.ishare.ui.theme.poorstoryFont
import com.ryun.ishare.viewmodel.IdeaListViewModel
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder
import com.ryun.ishare.ui.components.CountOverlay
import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.ryun.ishare.util.UserManager

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyIdeaDetailScreen(navController: NavController, ideaId: String?) {

//    val viewModel: IdeaListViewModel = viewModel() // IdeaListViewModel에 접근
//    val navBackStackEntry by navController.currentBackStackEntryAsState()

//    val categoryParamFromList = navBackStackEntry?.arguments?.getString("category") ?: "전체"
//    val subCategoryParamFromList = navBackStackEntry?.arguments?.getString("subCategory") ?: ""
    val visibleRepliesMap = remember { mutableStateMapOf<Pair<String, Int>, Boolean>() }

    val replyCounts = remember { mutableStateMapOf<String, Int>() }
    val commentLikeCounts = remember { mutableStateMapOf<String, Int>() }

    val commentLikeUsers = remember { mutableStateMapOf<String, MutableSet<String>>() }

    val isFocusedMap = remember { mutableStateMapOf<String, Boolean>() }

    var replyTarget by remember { mutableStateOf<Pair<String, Int>?>(null) }

    val activity = LocalContext.current as ComponentActivity
    val viewModel: IdeaListViewModel = viewModel(viewModelStoreOwner = activity)

    val bulbCounts = viewModel.bulbCounts
    val shareCounts = viewModel.shareCounts
    val commentCounts = viewModel.commentCounts

    var expandedCommentBoxId by remember { mutableStateOf<String?>(null) } // 열려 있는 댓글 박스 ID

    val userId = UserManager.currentUserId // 위에 선언된 걸 활용

    val context = LocalContext.current
    var shareDialogIdeaId by remember { mutableStateOf<String?>(null) }
    var pendingShareIdeaId by remember { mutableStateOf<String?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedSubCategory by remember { mutableStateOf(false) }

    val allCategories = remember {
        listOf(
            "전체",
            "일상생활",
            "창업",
            "디자인",
            "콘텐츠",
            "IT/기술",
            "운동/건강",
            "푸드/레시피",
            "가전/생활용품",
            "농업/어업/축산업",
            "교육",
            "환경/지속가능성",
            "여행"
        )
    }
    val subCategories = remember {
        mapOf(
            "일상생활" to listOf("전체", "생활용품", "가전", "청소", "꾸미기", "정리"),
            "창업" to listOf("전체", "스타트업", "비즈니스 아이디어", "투자", "아이템"),
            "디자인" to listOf("전체", "UI/UX", "그래픽", "제품"),
            "콘텐츠" to listOf("전체", "글쓰기", "영상", "사진"),
            "IT/기술" to listOf("전체", "프로그래밍", "데이터 분석", "네트워킹"),
            "운동/건강" to listOf("전체", "헬스", "요가", "필라테스", "러닝"),
            "푸드/레시피" to listOf("전체", "레시피", "베이킹", "음료"),
            "가전/생활용품" to listOf("전체", "주방가전", "생활가전", "인테리어"),
            "농업/어업/축산업" to listOf("전체", "작물 재배", "양식", "축산"),
            "교육" to listOf("전체", "어학", "코딩", "예술"),
            "환경/지속가능성" to listOf("전체", "재활용", "에너지", "친환경"),
            "여행" to listOf("전체", "국내 여행", "해외 여행", "캠핑", "드라이브")
        )
    }

//    val categoryInfoMap = remember {
//        mapOf(
//            "uniqueId1" to Pair("창업", "스타트업"),
//            "uniqueId2" to Pair("일상생`활", "생활용품"),
//            "uniqueId3" to Pair("일상생활", "가전"),
//            "uniqueId4" to Pair("일상생활", "청소"),
//            "uniqueId5" to Pair("창업", "스타트업"),
//            "uniqueId6" to Pair("환경/지속가능성", "재활용")
//        )
//    }

    val idea: Idea? = myDummyDatas[ideaId]

    val currentUser = "이강륜" // 현재 로그인된 사용자 이름이라고 가정

    val currentCategoryInfo = Pair(
        idea?.category ?: "일반",
        idea?.subCategory ?: ""
    )

    LaunchedEffect(Unit) {
        snapshotFlow { pendingShareIdeaId }
            .collectLatest { id ->
                id?.let {
                    myDetailShareIdea(context, "https://ishare.app/idea/$id")
                    pendingShareIdeaId = null // 다시 초기화
                }
            }
    }

    // ✅ 여기에 넣으세요
    LaunchedEffect(Unit) {
        commentMap.forEach { (ideaId, comments) ->
            comments.forEachIndexed { index, comment ->
                if (comment.startsWith("↳")) {
                    val parentIndex = comment.removePrefix("↳").substringBefore("|").toIntOrNull()
                    if (parentIndex != null) {
                        val key = "$ideaId-$parentIndex"
                        replyCounts[key] = (replyCounts[key] ?: 0) + 1
                    }
                }
            }
        }
    }

    val sidePadding = 16.5.dp
//    val ideaTop = 84.57.dp
    val ideaHeight = 65.41.dp
//    val contentTopPadding = ideaTop + ideaHeight + 22.54.dp
    val focusManager = LocalFocusManager.current
    var expandedDropdown by remember { mutableStateOf<String?>(null) }
    val onExpandedDropdownChange: (String?) -> Unit = { expandedDropdown = it }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1D8FF))
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    expandedDropdown = null // 🔥 드롭다운 닫기
                    focusManager.clearFocus() // 🔥 포커스도 해제
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // ✅ 이거 빠지면 다 밀려서 안 보임
//                .background(Color(0xFFF1D8FF))
                .padding(
                    start = sidePadding,
                    end = sidePadding,
                    //              top = contentTopPadding,
                    //                bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            Spacer(modifier = Modifier.height(84.57.dp))

            // ✅ 절대 좌표로 "Idea" 텍스트 위치 지정
            Box(
                modifier = Modifier
                    .size(width = 189.dp, height = ideaHeight)
            ) {
                // 로고 텍스트
                Text(
                    text = "My Idea", // 변경: "Idea" -> "My Idea"
                    fontSize = 40.sp,
                    fontFamily = shizuruFont,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable {
                            navController.navigate("my_idea") // 변경: "idea_list" -> "my_idea_list"
                        }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
////                    .padding(horizontal = 16.dp), // 양쪽 여백
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // 왼쪽: 카테고리 드롭다운 (Box로 감싸서 위치 고정)
//                Box(
//                    modifier = Modifier.weight(1f) // 남는 공간 확보
//                ) {
            MyDetailCategoryRowWithDropdowns(
                selectedCategory = currentCategoryInfo.first,
                selectedSubCategory = currentCategoryInfo.second,
                categories = allCategories,
                subCategories = subCategories[currentCategoryInfo.first] ?: emptyList(),
                expandedDropdown = expandedDropdown, // ✅ 이 줄 추가
                onExpandedDropdownChange = { expandedDropdown = it }, // ✅ 이 줄 추가
                onCategorySelect = { selected ->
                    Log.d(
                        "💡ViewModel",
                        "✅ addHistory 호출됨: ${currentCategoryInfo.first} / ${currentCategoryInfo.second}"
                    )
                    navController.navigate("my_idea?category=$selected")
                },
                onSubCategorySelect = { selected ->
                    Log.d(
                        "💡ViewModel",
                        "✅ 서브카테고리 선택됨: ${currentCategoryInfo.first} / $selected"
                    )
                    navController.navigate("my_idea?category=${currentCategoryInfo.first}&subCategory=$selected")
                }
            )
//                }
//                Text(
//                    text = "작성일: ${formatTimestamp(idea?.timestamp ?: System.currentTimeMillis())}",
//                    fontSize = 10.sp,
//                    color = Color.Gray,
////                    textAlign = TextAlign.End,
//                    modifier = Modifier
//                        .padding(start = 8.dp, top = 24.dp) // 약간 아래로
//                        .align(Alignment.Top) // 상단 정렬
//                )
//            }

            Spacer(
                modifier = Modifier.height(
                    when (expandedDropdown) {
                        "main", "sub" -> 20.dp
                        else -> 6.dp
                    }
                )
            )

            // 제목 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // 🔄 높이 자동 조절
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(vertical = 35.dp, horizontal = 20.dp)
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically // 🔥 전체 요소 상하 중앙 정렬
            ) {
                // 제목: 중앙 정렬
                Text(
                    text = idea?.title ?: "제목 없음",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 20.dp) // 🔹 좌우 간격만 적용
                )

                // 작성자: 오른쪽 상단 정렬
                Text(
                    text = idea?.author ?: "알 수 없음",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = (-30).dp) // 🔺 위로 4dp 올림
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            // 좋아요/공유/댓글 바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val bulbCount = bulbCounts[ideaId] ?: 0
                val shareCount = shareCounts[ideaId] ?: 0
                val commentCount = commentCounts[ideaId] ?: 0

//                Text(
//                    text = "${myFormatTimestamp(idea?.timestamp ?: System.currentTimeMillis())}",
//                    fontSize = 10.sp,
//                    color = Color.Gray,
////                    textAlign = TextAlign.End,
//                    modifier = Modifier
////                        .padding(start = 8.dp, top = 24.dp) // 약간 아래로
//                        .offset(y = -5.dp) // 🔽 아래로 4dp 이동
//                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // 전구 아이콘
                    Box(modifier = Modifier.size(20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_originalbulb),
                            contentDescription = "bulb",
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    onExpandedDropdownChange(null) // ✅ 부모 상태 변경 요청
                                    ideaId?.let {
                                        viewModel.incrementBulbCount(ideaId, userId)
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )
                        if (bulbCount > 0) {
                            CountOverlay(
                                count = bulbCount,
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-10).dp)
                            )
                        }
                    }

                    // 공유 아이콘
                    Box(modifier = Modifier.size(20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_originalshare),
                            contentDescription = "share",
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    onExpandedDropdownChange(null) // ✅ 부모 상태 변경 요청
                                    ideaId?.let {
                                        shareDialogIdeaId = ideaId
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )
                        if (shareCount > 0) {
                            CountOverlay(
                                count = shareCount,
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-10).dp)
                            )
                        }
                    }

                    if (shareDialogIdeaId == ideaId) {
                        MyDetailShareIdeaDialog(
                            onConfirm = {
                                ideaId?.let {
                                    viewModel.incrementShareCount(ideaId)
                                }
                                pendingShareIdeaId = ideaId
                                shareDialogIdeaId = null
                            },
                            onDismiss = {
                                shareDialogIdeaId = null
                            }
                        )
                    }

                    // 댓글 아이콘
                    Box(modifier = Modifier.size(20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_originalcomment),
                            contentDescription = "comment",
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    ideaId?.let {
                                        onExpandedDropdownChange(null) // ✅ 부모 상태 변경 요청
                                        expandedCommentBoxId =
                                            if (expandedCommentBoxId == ideaId) null else ideaId
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )
                        if (commentCount > 0) {
                            CountOverlay(
                                count = commentCount,
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-10).dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (!ideaId.isNullOrEmpty() && expandedCommentBoxId == ideaId) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
//                                color = Color(0xFFFFF4E1), //크림톤
//                                color = Color(0xFFFFFBF6), //매우 연한 베이지
//                                color = Color(0xFFF0F0F0), //연한 회색
//                                color = Color(0xFFFFFCF9), //조금 더 연한 베이지

//                                color = Color(0xFFF9F3EC), //조금 더 진한 베이지
                    color = Color.White.copy(alpha = 0.6f), //흰색

                    tonalElevation = 2.dp,     // 선택사항: 그림자 효과
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        val comments = commentMap[ideaId] ?: emptyList()


                        val commentScrollState = rememberScrollState()

                        // ✅ 댓글 스크롤 중 포커스 해제
                        LaunchedEffect(commentScrollState) {
                            snapshotFlow { commentScrollState.isScrollInProgress }
                                .collect { isScrolling ->
                                    if (isScrolling) {
                                        focusManager.clearFocus()
//                                                    expandedCommentBoxId = null
//                                                    replyTarget = null
                                    }
                                }
                        }

                        LaunchedEffect(commentMap[ideaId]?.size) {
                            commentScrollState.animateScrollTo(commentScrollState.maxValue)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp) // ✅ 최대 높이 제한 (3줄 정도)
                                .verticalScroll(commentScrollState)
                        ) {
                            Column {
                                if (comments.isEmpty()) {
                                    Text(
                                        "아직 댓글이 없습니다.",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                } else {
                                    comments.forEachIndexed { index, comment ->
                                        if (comment.startsWith("↳")) return@forEachIndexed // ⛔ 답글은 여기서 건너뜀

                                        val commentKey = "$ideaId-$index"
                                        val likeCount =
                                            commentLikeCounts[commentKey] ?: 0
                                        val replyCount =
                                            replyCounts.getOrElse(commentKey) { 0 }
                                        val key = "$ideaId-$index"

                                        val isReply =
                                            comment.startsWith("↳") // 저장된 댓글이 답글인지 확인
                                        val prefix =
                                            if (isReply) "↪ " else "\uD83D\uDCCC "
//                                                    💬
//                                                    val bgColor = if (isReply) Color(0xFFFFF9F0) else Color.Transparent
                                        val indent = if (isReply) 12.dp else 0.dp
                                        val displayText =
                                            if (isReply) comment.removePrefix("↳ ")
                                                .trim() else comment

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
//                                                            .background(bgColor)
                                                .padding(
                                                    start = indent,
                                                    top = 4.dp,
                                                    bottom = 4.dp
                                                ),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(modifier = Modifier.weight(1f))
                                            {
                                                // ⬅️ 닉네임 박스 (왼쪽)
                                                if (isReply) {
                                                    Text(
                                                        "╰▷",
//                                                                    ╰▶, ╰▸, ╰▷
                                                        fontSize = 12.sp, // 기존보다 크게!
                                                        fontWeight = FontWeight.Bold, // 두껍게!
                                                        color = Color(0xFF555555), // 더 진한 회색톤으로 강조
                                                        modifier = Modifier.padding(end = 6.dp)
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = if (isReply) Color.White else Color(
                                                                0xFFFFF7B0
                                                            ),
                                                            shape = RoundedCornerShape(
                                                                12.dp
                                                            )
                                                        )
                                                        .padding(
                                                            horizontal = 4.dp,
//                                                                        vertical = 4.dp
                                                        )
                                                        .defaultMinSize(minHeight = 24.dp)
                                                ) {
                                                    Text(
                                                        text = "DFEDFEDFffkk", // 💡 여기에 댓글 작성자 닉네임을 넣으세요 (현재 구조에서는 하드코딩됨)
                                                        fontSize = 10.sp,
                                                        fontFamily = poorstoryFont,
                                                        color = Color.Black
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Text(
                                                    text = displayText,
                                                    fontSize = 12.sp,
//                                                                fontFamily = poorstoryFont,
                                                    color = Color.Black,
                                                    modifier = Modifier
                                                        .weight(1f), // 오른쪽 공간 차지
                                                    maxLines = 3, // 원한다면 제한
                                                    overflow = TextOverflow.Clip
                                                )
                                            }
// 🔽 바로 여기

                                            // ➡️ 오른쪽: ❤️ + 💬
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    8.dp
                                                )
                                            ) {
                                                // ❤️ 좋아요
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable {
//                                                                    val key = "$ideaId-$index"
                                                        val likedUsers =
                                                            commentLikeUsers.getOrPut(
                                                                key
                                                            ) { mutableSetOf() }

                                                        if (!likedUsers.contains(userId)) {
                                                            commentLikeCounts[key] =
                                                                (commentLikeCounts[key]
                                                                    ?: 0) + 1
                                                            likedUsers.add(userId)
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Favorite,
                                                        contentDescription = "좋아요",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = likeCount.toString(),
                                                        fontSize = 11.sp,
                                                        color = Color.Black
                                                    )
                                                }
                                                if (!isReply) {
                                                    // 💬 답글
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.clickable {
                                                            val key = ideaId to index
                                                            val isCurrentlyReplying = replyTarget == key
                                                            val isReplyVisible = visibleRepliesMap[key] == true

                                                            if (isCurrentlyReplying) {
                                                                replyTarget = null
                                                                visibleRepliesMap[key] = false
                                                            } else {
                                                                // 다른 답글이 열려 있으면 모두 닫기
                                                                visibleRepliesMap.keys.forEach { existingKey ->
                                                                    visibleRepliesMap[existingKey] = false
                                                                }
                                                                // 현재 선택된 댓글만 열기
                                                                replyTarget = key
                                                                visibleRepliesMap[key] = true
                                                            }
                                                        }
                                                    ) {
                                                        Text(
                                                            text = "💬 $replyCount",
                                                            fontSize = 12.sp,
                                                            color = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (visibleRepliesMap[ideaId to index] == true) {
                                            val replies = comments
                                                .withIndex()
                                                .filter { (_, value) ->
                                                    value.startsWith("↳$index|")
                                                }

                                            replies.forEach { (replyIndex, replyComment) ->
                                                val replyText = replyComment
                                                    .removePrefix("↳$index|")
                                                    .trim()
                                                val replyKey = "$ideaId-$replyIndex"
                                                val likeCount =
                                                    commentLikeCounts[replyKey] ?: 0

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            start = 24.dp,
                                                            top = 4.dp
                                                        ),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    // 왼쪽: 화살표 + 닉네임 + 텍스트
                                                    Row(modifier = Modifier.weight(1f)) {
                                                        // 🔸 꺾이는 화살표
                                                        Text(
                                                            text = "╰▷",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF555555),
                                                            modifier = Modifier.padding(
                                                                end = 6.dp
                                                            )
                                                        )

                                                        // 🔸 닉네임 박스
                                                        Box(
                                                            modifier = Modifier
                                                                .background(
                                                                    color = Color.White,
                                                                    shape = RoundedCornerShape(
                                                                        12.dp
                                                                    )
                                                                )
                                                                .padding(horizontal = 4.dp)
                                                                .defaultMinSize(
                                                                    minHeight = 24.dp
                                                                )
                                                        ) {
                                                            Text(
                                                                text = "DFEDFEDFffkk", // ✅ 닉네임으로 변경 가능
                                                                fontSize = 10.sp,
                                                                fontFamily = poorstoryFont,
                                                                color = Color.Black
                                                            )
                                                        }

                                                        Spacer(
                                                            modifier = Modifier.width(
                                                                8.dp
                                                            )
                                                        )

                                                        // 🔸 답글 텍스트
                                                        Text(
                                                            text = replyText,
                                                            fontSize = 12.sp,
                                                            color = Color.Black,
                                                            maxLines = 3,
                                                            overflow = TextOverflow.Clip,
                                                            modifier = Modifier.weight(
                                                                1f
                                                            )
                                                        )
                                                    }

                                                    // 오른쪽: ❤️ 좋아요
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.clickable {
                                                            val likedUsers =
                                                                commentLikeUsers.getOrPut(
                                                                    replyKey
                                                                ) { mutableSetOf() }
                                                            if (!likedUsers.contains(
                                                                    userId
                                                                )
                                                            ) {
                                                                commentLikeCounts[replyKey] =
                                                                    likeCount + 1
                                                                likedUsers.add(userId)
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Favorite,
                                                            contentDescription = "좋아요",
                                                            tint = Color.Red,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(
                                                            modifier = Modifier.width(
                                                                3.dp
                                                            )
                                                        )
                                                        Text(
                                                            text = likeCount.toString(),
                                                            fontSize = 11.sp,
                                                            color = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                            if (replies.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }
//                                                    Spacer(modifier = Modifier.height(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val commentInput = newCommentMap[ideaId] ?: ""
                        val isFocused = remember { mutableStateOf(false) } // ✅ 요기 선언

                        // ✅ 여기 아래에 선언하면 됨!
                        val isReplying = replyTarget?.first == ideaId && expandedCommentBoxId == ideaId
                        val placeholderText = if (isReplying) "답글을 입력하세요" else "댓글을 입력하세요"

                        val confirmButtonColors = if (isReplying) {
                            ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEFD7FF),
                                contentColor = Color.Black
                            )
                        }

                        val cancelButtonColors = if (isReplying) {
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEFD7FF),
                                contentColor = Color.Black
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isReplying) {
                                // 🔁 답글 상태: 버튼 먼저, 입력창 마지막
                                Button(
                                    onClick = {
                                        if (commentInput.isNotBlank()) {
                                            val updated = commentMap.getOrPut(ideaId) { mutableListOf() }
                                            val text = commentInput.trim()
                                            if (isReplying) {
                                                val parentIdx = replyTarget!!.second
                                                updated.add("↳$parentIdx|$text") // ✅ 댓글 인덱스를 붙여서 저장

                                                // 🔥 여기서 replyCount 증가!
                                                val replyKey = "$ideaId-$parentIdx"
                                                replyCounts[replyKey] = (replyCounts[replyKey] ?: 0) + 1

                                                visibleRepliesMap[ideaId to parentIdx] = true // ✅ 답글 펼쳐지도록 상태 설정
                                                replyTarget = ideaId to parentIdx // 🔥 replyTarget 유지시킴 (null로 하지 않음)
                                            } else {
                                                updated.add(text)
                                                viewModel.incrementCommentCount(ideaId)
                                            }
                                            newCommentMap[ideaId] = ""

                                            focusManager.clearFocus() // ✅ 입력 후 포커스 해제
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .defaultMinSize(minWidth = 60.dp), // 버튼 너비 최소값 설정
                                    colors = confirmButtonColors,
                                    border = BorderStroke(1.dp, Color.Black),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 0.dp
                                    )
                                ) {
                                    Text(
                                        "답글 등록",
                                        fontSize = 13.sp,
                                        fontFamily = poorstoryFont
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                OutlinedButton(
                                    onClick = {
                                        if (replyTarget?.first == ideaId) {
//                                                        // 👉 답글 등록 상태일 경우: 답글 모두 제거
//                                                        commentMap[ideaId] = commentMap[ideaId]
//                                                            ?.filterNot { it.startsWith("↳") }?.toMutableList() ?: mutableListOf()
                                            val (targetIdeaId, targetIndex) = replyTarget!!

                                            // 🔥 답글 리스트 접기
                                            visibleRepliesMap[targetIdeaId to targetIndex] = false

                                            // 👉 상태 초기화 (답글 등록 상태 → 일반 댓글 상태로 전환)
                                            replyTarget = null
                                            newCommentMap[ideaId] = ""
                                        } else {
                                            // 👉 일반 댓글 입력 상태일 경우: 댓글창만 닫기
                                            expandedCommentBoxId = null
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .defaultMinSize(minWidth = 60.dp),
                                    colors = cancelButtonColors,
                                    border = BorderStroke(1.dp, Color.Black),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 0.dp
                                    )
                                ) {
                                    Text(
                                        "닫기",
                                        fontSize = 13.sp,
                                        fontFamily = poorstoryFont
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .background(
                                            Color.White,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                            contentAlignment = Alignment.CenterStart
                                ) {
                                    if (commentInput.isEmpty() && !isFocusedMap.getOrElse(
                                            ideaId
                                        ) { false }
                                    ) {
                                        Text(
                                            text = placeholderText,
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier
                                                .align(Alignment.CenterStart) // ✅ 왼쪽 + 상하 중앙
                                                .offset(y = (-1.5).dp) // 🔥 상단으로 살짝 올리기
                                        )
                                    }

                                    BasicTextField(
                                        value = commentInput,
                                        onValueChange = { newCommentMap[ideaId] = it },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            fontSize = 13.sp,
                                            color = Color.Black
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterStart) // ✅ 입력 중에도 동일한 위치 유지
                                            .onFocusChanged {
                                                isFocusedMap[ideaId] = it.isFocused
                                                if (it.isFocused) onExpandedDropdownChange(
                                                    null
                                                ) // 🔥 드롭다운 닫기 추가
                                            }
                                    )
                                }
                            } else {
                                // 💬 댓글 상태: 입력창 먼저
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    if (commentInput.isEmpty() && !isFocusedMap.getOrElse(ideaId) { false }) {
                                        Text(
                                            text = placeholderText,
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .offset(y = (-1.5).dp)
                                        )
                                    }

                                    BasicTextField(
                                        value = commentInput,
                                        onValueChange = { newCommentMap[ideaId] = it },
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 13.sp, color = Color.Black),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterStart)
                                            .onFocusChanged {
                                                isFocusedMap[ideaId] = it.isFocused
                                                if (it.isFocused) onExpandedDropdownChange(null)
                                            }
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (commentInput.isNotBlank()) {
                                            val updated =
                                                commentMap.getOrPut(ideaId) { mutableListOf() }
                                            val text = commentInput.trim()
                                            if (isReplying) {
                                                val parentIdx = replyTarget!!.second
                                                updated.add("↳$parentIdx|$text") // ✅ 댓글 인덱스를 붙여서 저장

                                                // 🔥 여기서 replyCount 증가!
                                                val key = "$ideaId-$parentIdx"
                                                replyCounts[key] =
                                                    (replyCounts[key] ?: 0) + 1

                                                //                                                        replyTarget = null
                                            } else {
                                                updated.add(text)
                                                viewModel.incrementCommentCount(ideaId)
                                            }
                                            newCommentMap[ideaId] = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .defaultMinSize(minWidth = 60.dp), // 버튼 너비 최소값 설정
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 0.dp
                                    ),
                                    colors = confirmButtonColors,
                                    border = BorderStroke(
                                        1.dp,
                                        Color.Black
                                    ) // ✅ 테두리 색과 두께
                                ) {
                                    Text(
                                        text = if (isReplying) "답글 등록" else "등록",
                                        fontSize = 13.sp,
                                        fontFamily = poorstoryFont
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                OutlinedButton(
                                    onClick = {
                                        if (replyTarget?.first == ideaId) {
                                            // 👉 답글 등록 상태일 경우: 답글 모두 제거
                                            commentMap[ideaId] = commentMap[ideaId]
                                                ?.filterNot { it.startsWith("↳") }
                                                ?.toMutableList() ?: mutableListOf()

                                            // 👉 상태 초기화 (답글 등록 상태 → 일반 댓글 상태로 전환)
                                            replyTarget = null
                                            newCommentMap[ideaId] = ""
                                        } else {
                                            // 👉 일반 댓글 입력 상태일 경우: 댓글창만 닫기
                                            expandedCommentBoxId = null
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .defaultMinSize(minWidth = 60.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 0.dp
                                    ),
                                    colors = cancelButtonColors,
                                    border = BorderStroke(
                                        1.dp,
                                        Color.Black
                                    ) // ✅ 테두리 색과 두께
                                ) {
                                    Text(
                                        "닫기",
                                        fontSize = 13.sp,
                                        fontFamily = poorstoryFont
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp)) // 드롭다운 ↔ 콘텐츠 사이 간격
            } else {
                Spacer(modifier = Modifier.height(40.dp)) // 아이콘 ↔ 콘텐츠 기본 간격
            }

            // Contents 박스
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp) // ✅ 최대 높이는 350dp
//                  .height(350.dp) // ✅ 고정 높이로 실험
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp),
//                contentAlignment = Alignment.TopStart // 수정된 부분: 내용 좌측 상단 정렬
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = idea?.content ?: "내용 없음",
                        color = Color.Black,
                        fontSize = 13.sp,
                        lineHeight = 24.sp
//                        textAlign = TextAlign.Start,
//                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "${myFormatTimestamp(idea?.timestamp ?: System.currentTimeMillis())}",
                        fontSize = 10.sp,
                        color = Color.Gray,
//                    textAlign = TextAlign.End,
                        modifier = Modifier
                            .align(Alignment.End)
//                      .padding(end = 0.dp, bottom = 0.dp)
                        .offset(y = 10.dp) // 🔽 아래로 4dp 이동
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            // 편집 및 삭제 아이콘 바 (Contents 박스 아래)
            if (idea?.author == currentUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(horizontal = sidePadding),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit Idea",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                showEditDialog = true
                            },
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete Idea",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                showDialog = true // 삭제할 때 다이얼로그 열기
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        if (showDialog) {
            MyDetailDeleteIdeaDialog(
                onConfirm = {
                    showDialog = false
                    dummyDatas.remove(ideaId) // 🔥 진짜 삭제
                    navController.popBackStack() // 뒤로 가기
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }

        if (showEditDialog) {
            MyDetailEditIdeaDialog(
                onConfirm = {
                    showEditDialog = false

                    // ✅ 인코딩 처리 후 수정화면 이동
                    val encodedTitle = URLEncoder.encode(idea?.title ?: "", "UTF-8").replace("+", "%20")
                    val encodedContent = URLEncoder.encode(idea?.content ?: "", "UTF-8").replace("+", "%20")
                    val encodedCategory = URLEncoder.encode(idea?.category ?: "", "UTF-8")
                    val encodedSubCategory = URLEncoder.encode(idea?.subCategory ?: "", "UTF-8")

                    navController.navigate(
                        "idea_write?ideaId=$ideaId&title=$encodedTitle&content=$encodedContent&category=$encodedCategory&subCategory=$encodedSubCategory"
                    )
                },
                onDismiss = {
                    showEditDialog = false
                }
            )
        }

        // ✅ 하단 내비게이션은 화면 최하단 고정용 Box로 분리
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // 내부 아이콘 크기에 맞게 Row 높이 조절
                    .background(Color(0xFFEFD7FF))
                    .padding(vertical = 30.dp), // 약간의 여백만 줄
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔙 Back 아이콘
                Box(
                    modifier = Modifier.padding(top = 30.dp) // ⬅️ Back만 위로 올림
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                navController.popBackStack() // IdeaListScreen으로 돌아가기
                            },
                        contentScale = ContentScale.Fit
                    )
                }
                // 🥚 Egg 아이콘 (중앙)
                Box(
                    modifier = Modifier.padding(top = 0.dp) // 필요 시 조정
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_egg),
                        contentDescription = "Egg",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                navController.navigate("main")
                            },
                        contentScale = ContentScale.Fit
                    )
                }
                Box(
                    modifier = Modifier.padding(top = 30.dp) // ⬅️ Idea는 살짝 위로 조정
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_idea),
                        contentDescription = "New Idea",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                navController.navigate("idea_write") // ✅ 여기로 이동!
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

//@Composable
//fun MyCategoryButton(text: String, selected: Boolean) {
//    val backgroundColor = if (selected) Color(0xFFFFFF99) else Color.White
//    val textColor = if (selected) Color.Black else Color.Gray
//
//    Box(
//        modifier = Modifier
//            .background(backgroundColor, shape = RoundedCornerShape(50))
//            .padding(horizontal = 20.dp, vertical = 8.dp)
//    ) {
//        Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
//    }
//}

// ⬇️ 파일 맨 아래나, @Composable 위에 추가
fun myFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    return sdf.format(Date(timestamp))
}

//@Composable
//fun MyDetailCountOverlay(count: Int, modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier.size(20.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.ic_plus),
//            contentDescription = "plus",
//            modifier = Modifier.fillMaxSize()
//        )
//        Text(
//            text = "$count",
//            fontSize = 10.sp,
////            fontWeight = FontWeight.Bold,
//            color = Color.Black,
//            modifier = Modifier
//                .align(Alignment.Center) // 기본적으로 가운데 정렬
//                .offset(y = (-1.dp)) // 위로 살짝 올리기
//        )
//    }
//}

@Composable
fun MyDetailCategoryRowWithDropdowns(
    selectedCategory: String,
    selectedSubCategory: String,
    categories: List<String>,
    subCategories: List<String>,
    expandedDropdown: String?, // ✅ 추가
    onExpandedDropdownChange: (String?) -> Unit, // ✅ 추가
    onCategorySelect: (String) -> Unit,
    onSubCategorySelect: (String) -> Unit,
) {
    // "main", "sub", null 중 하나
    Column {
        // 첫 줄: 1차/2차 나란히 배치
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1차 카테고리 박스
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onExpandedDropdownChange(if (expandedDropdown == "main") null else "main") // ✅ 수정
                    },
                color = Color(0xFFFFF779),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .height(34.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCategory,
                        fontSize = 15.sp,
                        fontFamily = poorstoryFont,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                        contentDescription = "카테고리 드롭다운"
                    )
                }
            }

            // 2차 카테고리 박스
            if (selectedSubCategory.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onExpandedDropdownChange(if (expandedDropdown == "sub") null else "sub") // ✅ 여기도!
                        },
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .height(34.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedSubCategory.isEmpty()) "전체" else selectedSubCategory,
                            fontSize = 15.sp,
                            fontFamily = poorstoryFont,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                            contentDescription = "서브카테고리 드롭다운"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        // 아래 펼쳐지는 드롭다운 (하나만 보여줌)
        AnimatedVisibility(visible = expandedDropdown == "main") {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory

                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFFFF779) else Color.White)
                            .clickable {
                                onCategorySelect(category)
                                onExpandedDropdownChange(null) // ✅ 부모 상태 변경 요청
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 15.sp,
                            fontFamily = poorstoryFont,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = expandedDropdown == "sub" && selectedCategory != "전체") {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(subCategories) { subCategory ->
                    val isSelected = (subCategory == "전체" && selectedSubCategory.isEmpty()) ||
                            (subCategory == selectedSubCategory)

                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color.White else Color(0xFFFFF779))
                            .clickable {
                                onSubCategorySelect(if (subCategory == "전체") "" else subCategory)
                                onExpandedDropdownChange(null) // ✅ 부모 상태 변경 요청
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subCategory,
                            fontSize = 15.sp,
                            fontFamily = poorstoryFont,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

fun myDetailShareIdea(context: android.content.Context, link: String) {
    val intent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
    val chooser = android.content.Intent.createChooser(intent, "공유 방법 선택")
    context.startActivity(chooser)
}

@Composable
fun MyDetailShareIdeaDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFFBFB),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // 제목
                Text(
                    text = "아이디어 공유",
                    fontSize = 17.sp,
                    fontFamily = poorstoryFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 내용
                Text(
                    text = "이 아이디어를 공유하시겠습니까?",
                    fontSize = 13.sp,
                    fontFamily = poorstoryFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 2개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFD7FF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp), // ✅ 패딩 제거
                        border = BorderStroke(1.dp, Color.Black) // ✅ 테두리 색과 두께
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight() // ✅ fillMaxSize ❌
                                .wrapContentWidth(), // ✅ 콘텐츠 너비만큼만
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "공유",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp), // ✅ 패딩 제거
                        border = BorderStroke(1.dp, Color.Black) // ✅ 테두리 색과 두께
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight() // ✅ fillMaxSize ❌
                                .wrapContentWidth(), // ✅ 콘텐츠 너비만큼만
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "취소",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyDetailDeleteIdeaDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFFBFB),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // 제목
                Text(
                    text = "아이디어 삭제",
                    fontSize = 17.sp,
                    fontFamily = poorstoryFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 내용
                Text(
                    text = "이 아이디어를 삭제하시겠습니까?",
                    fontSize = 13.sp,
                    fontFamily = poorstoryFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 2개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFD7FF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "삭제",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "취소",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyDetailEditIdeaDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFFBFB),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // 제목
                Text(
                    text = "아이디어 수정",
                    fontSize = 17.sp,
                    fontFamily = poorstoryFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 내용
                Text(
                    text = "이 아이디어를 수정하시겠습니까?",
                    fontSize = 13.sp,
                    fontFamily = poorstoryFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 2개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFD7FF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "수정",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "취소",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }
                }
            }
        }
    }
}
 */