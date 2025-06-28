package com.ryun.ishare.ui.screens

// ✅ 기본 Android & Compose 라이브러리
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// ✅ Lifecycle & Navigation 관련
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ryun.ishare.R
import kotlinx.coroutines.launch

// ✅ 프로젝트 내부 모듈
import com.ryun.ishare.ui.components.IdeaBottomBar
import com.ryun.ishare.ui.components.IdeaListContent
import com.ryun.ishare.ui.components.IdeaListHeader
import com.ryun.ishare.ui.extensions.clearFocusOnTap
import com.ryun.ishare.ui.theme.BackgroundColor
import com.ryun.ishare.ui.theme.Dimens
import com.ryun.ishare.util.UserManager
import com.ryun.ishare.util.shareIdea
import com.ryun.ishare.viewmodel.IdeaListViewModel

@Composable
fun MyIdeaScreen(navController: NavController) {

    // ✅ ViewModel 및 Context, FocusManager 초기화
    val viewModel: IdeaListViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val userId = UserManager.currentUserId

    // ✅ 현재 네비게이션 백스택 참조 (Detail → List 복귀 시 데이터 처리용)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // ✅ ViewModel에서 상태 수집 (uiState는 리스트 필터링, 입력값 등)
    val uiState by viewModel.uiState.collectAsState()
    val shareUIState by viewModel.shareUIState.collectAsState()
    val myIdeas by viewModel.myFilteredIdeas.collectAsState()

    // ✅ 리스트 스크롤 상태 및 하단 패딩 설정
    val listState = rememberLazyListState()
    val isAtEnd by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == myIdeas.lastIndex
        }
    }
    val sidePadding = Dimens.AllSidePadding
    val bottomPadding = remember(isAtEnd) {
        if (isAtEnd) Dimens.BottomPaddingAtEnd else Dimens.BottomPaddingDefault
    }

    // ✅ 최초 진입 및 상태 초기화 처리
    LaunchedEffect(Unit) {
        // 🔹 Detail → List로 돌아왔을 때 전달된 카테고리 처리
        viewModel.handleNavigationArgumentsFrom(navBackStackEntry)

        // 🔹 공유 인텐트 처리 (ViewModel에서 감지되면 바로 공유)
        launch {
            viewModel.observePendingShareIntent(
                context = context,
                onComplete = { ideaId ->
                    shareIdea(context, "https://ishare.app/idea/$ideaId")
                }
            )
        }

        // 🔹 댓글 수 초기화 (댓글/답글 리스트 표시 시 필요)
        viewModel.initReplyCountMapFrom(viewModel.commentMap)
    }

    // ✅ 전체 화면 구조 정의
    Scaffold(
        bottomBar = {
            // 하단 바 버튼들 (뒤로, 메인, 작성)
            IdeaBottomBar(
                onBackClick = { navController.popBackStack() },
                onMainClick = { navController.navigate("main") },
                onWriteClick = { navController.navigate("idea_write") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTap( // 화면 아무 곳 클릭 시 포커스 해제 + 드롭다운/댓글창 닫기
                    focusManager = focusManager,
                    viewModel = viewModel,
                    expandedCommentBoxId = viewModel.expandedCommentBoxId.collectAsState().value // ✅ OK
                )
                .background(BackgroundColor) // 전체 배경 유지
        ) {
            // 🟣 중앙 정렬된 이미지 (ic_idea)
// 1️⃣ Column 내부에서 이미지와 나머지 컴포넌트를 함께 다룸
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 2️⃣ 상단 아이콘
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_idea),
                        contentDescription = "My Idea",
                        modifier = Modifier
                            .size(width = 186.8.dp, height = 169.dp)
                            .clickable {
                                navController.navigate("idea_write")
                            }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundColor)
                        .padding(horizontal = 16.5.dp)
                ) {
                    Spacer(modifier = Modifier.height(60.dp)) // 이미지와 텍스트 사이

                    // ✅ 상단 헤더 영역: 카테고리/검색/초기화
                    IdeaListHeader(
                        uiState = uiState,
                        onExpandedDropdownChange = { viewModel.setExpandedDropdown(it) },
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onSubCategorySelect = { viewModel.selectSubCategory(it) },
                        onSearchIconClick = { focusManager.clearFocus() },
                        onResetClick = { viewModel.onReset(listState) },
                        viewModel = viewModel,
                        focusManager = focusManager
                    )

                    // ✅ 댓글 관련 상태 묶음
                    val commentUIState = viewModel.getCommentUIState()

                    // ✅ 본문: 아이디어 카드 리스트
                    IdeaListContent(
                        ideas = myIdeas,
                        listState = listState,
                        bottomPadding = bottomPadding,
                        navController = navController,
                        viewModel = viewModel,
                        focusManager = focusManager,
                        userId = userId,
                        commentUIState = commentUIState,
                        shareUIState = shareUIState
                    )
                }
            }
        }
    }
}