package com.ryun.ishare.ui.screens

// ✅ 기본 Android & Compose 라이브러리
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel

// ✅ Lifecycle & Navigation 관련
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ryun.ishare.ui.components.DefaultBottomBar

// ✅ 프로젝트 내부 모듈
import com.ryun.ishare.ui.components.IdeaListContent
import com.ryun.ishare.ui.components.IdeaListHeader
import com.ryun.ishare.ui.extensions.HandleScrollEffect
import com.ryun.ishare.ui.extensions.clearFocusOnTap
import com.ryun.ishare.ui.theme.BackgroundColor
import com.ryun.ishare.ui.theme.Dimens
import com.ryun.ishare.util.UserManager
import com.ryun.ishare.util.shareIdea
import com.ryun.ishare.viewmodel.IdeaListViewModel

@Composable
fun IdeaListScreen(
    navController: NavController,
    viewModel: IdeaListViewModel = hiltViewModel()
) {

    // ✅ ViewModel 및 Context, FocusManager 초기화
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val userId = UserManager.currentUserId

    // ✅ 댓글 관련 상태 묶음
    val commentUIState = viewModel.getCommentUIState()

    // ✅ 현재 네비게이션 백스택 참조 (Detail → List 복귀 시 데이터 처리용)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // ✅ ViewModel에서 상태 수집 (uiState는 리스트 필터링, 입력값 등)
    val uiState by viewModel.uiState.collectAsState()
    val shareUIState by viewModel.shareUIState.collectAsState()
    val filteredIdeas by viewModel.filteredIdeas.collectAsState()

    // ✅ 리스트 스크롤 상태 및 하단 패딩 설정
    val listState = rememberLazyListState()
    val isAtEnd by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == filteredIdeas.lastIndex
        }
    }
    val sidePadding = Dimens.AllSidePadding
    val bottomPadding = remember(isAtEnd) {
        if (isAtEnd) Dimens.BottomPaddingAtEnd else Dimens.BottomPaddingDefault
    }

    val expandedCommentBoxId by commentUIState.expandedCommentBoxId.collectAsState()

    HandleScrollEffect(
        listState = listState,
        viewModel = viewModel,
        focusManager = focusManager
    )

    LaunchedEffect(Unit) {
        viewModel.initializeScreen(
            context = context,
            navBackStackEntry = navBackStackEntry,
            onShare = { ideaId ->
                shareIdea(context, "https://ishare.app/idea/$ideaId")
            }
        )
    }

    // ✅ 전체 화면 구조 정의
    Scaffold(
        bottomBar = { DefaultBottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTap( // 화면 아무 곳 클릭 시 포커스 해제 + 드롭다운/댓글창 닫기
                    focusManager = focusManager,
                    viewModel = viewModel,
                    expandedCommentBoxId = expandedCommentBoxId // ✅ 안전하게 전달
                )
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = sidePadding,
                        end = sidePadding,
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
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

                // ✅ 본문: 아이디어 카드 리스트
                IdeaListContent(
                    ideas = filteredIdeas,
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
