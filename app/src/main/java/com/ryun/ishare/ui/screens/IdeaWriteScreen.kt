package com.ryun.ishare.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults // 꼭 import
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.ryun.ishare.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.zIndex
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import com.ryun.ishare.ui.data.dummyDatas
import com.ryun.ishare.ui.data.myDummyDatas
import com.ryun.ishare.model.Idea
import com.ryun.ishare.util.UserManager // 꼭 import
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.ryun.ishare.ui.theme.poorstoryFont
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryun.ishare.ui.theme.shizuruFont
import com.ryun.ishare.viewmodel.IdeaListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaWriteScreen(
    navController: NavController,
    ideaId: String? = null,   // 🟡 요거 추가
    title: String? = null,
    content: String? = null,
    category: String? = null,
    subCategory: String? = null
) {
//    var selectedCategory by remember { mutableStateOf("") }
//    var selectedSubCategory by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf<String?>(null) }
//    var patentExpanded by remember { mutableStateOf(false) }
//    var patentDropdownExpanded by remember { mutableStateOf(false) } // 👈 이걸 쓰고 있음!

    var showDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showCategoryError by remember { mutableStateOf(false) }
    var showSubCategoryError by remember { mutableStateOf(false) }
    var showPatentError by remember { mutableStateOf(false) }

    // 1. 상태 선언
    var ideaTitle by remember {
        mutableStateOf(
            if (!title.isNullOrBlank()) TextFieldValue(title) else TextFieldValue("")
        )
    }
    var ideaContent by remember {
        mutableStateOf(
            if (!content.isNullOrBlank()) TextFieldValue(content) else TextFieldValue("")
        )
    }

    var selectedCategory by remember {
        mutableStateOf(category?.takeIf { it.isNotBlank() } ?: "")
    }
    var selectedSubCategory by remember {
        mutableStateOf(subCategory?.takeIf { it.isNotBlank() } ?: "")
    }

//    var selectedCategory by remember { mutableStateOf(category ?: "") }
//    var selectedSubCategory by remember { mutableStateOf(subCategory ?: "") }

    var selectedPatent by remember { mutableStateOf("특허 여부") }
//    var title by remember { mutableStateOf("") }
//    var content by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }

    var patentDropdownExpanded by remember { mutableStateOf(false) }

    val categoryList = listOf("전체") + listOf(
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

    val subCategories = mapOf(
        "일상생활" to listOf("생활용품", "가전", "청소", "꾸미기", "정리"),
        "창업" to listOf("스타트업", "비즈니스 아이디어", "투자", "아이템"),
        "디자인" to listOf("UI/UX", "그래픽", "제품"),
        "콘텐츠" to listOf("글쓰기", "영상", "사진"),
        "IT/기술" to listOf("프로그래밍", "데이터 분석", "네트워킹"),
        "운동/건강" to listOf("헬스", "요가", "필라테스", "러닝"),
        "푸드/레시피" to listOf("레시피", "베이킹", "음료"),
        "가전/생활용품" to listOf("주방가전", "생활가전", "인테리어"),
        "농업/어업/축산업" to listOf("작물 재배", "양식", "축산"),
        "교육" to listOf("어학", "코딩", "예술"),
        "환경/지속가능성" to listOf("재활용", "에너지", "친환경"),
        "여행" to listOf("국내 여행", "해외 여행", "캠핑", "드라이브")
    )

    val sidePadding = 16.5.dp
    val focusManager = LocalFocusManager.current
    val onExpandedDropdownChange: (String?) -> Unit = { expandedDropdown = it }

    var triggerSubDropdown by remember { mutableStateOf(false) }
    var backEnabled by remember { mutableStateOf(true) }
    val viewModel: IdeaListViewModel = viewModel()
    val history by viewModel.history.collectAsState()
    var hasTriedToSave by remember { mutableStateOf(false) }
    var titleFocused by remember { mutableStateOf(false) }
    var contentFocused by remember { mutableStateOf(false) }

    val bottomBarBackClick: () -> Unit = {

        if (backEnabled) {
            backEnabled = false

            coroutineScope.launch {
                if (history.isNotEmpty()) {
                    viewModel.goBack()
//                    delay(150) // UI가 반영될 시간 (recomposition)
                } else {
                    navController.popBackStack()
                }
                backEnabled = true
            }
        }
    }

    // IdeaWriteScreen 내부
    LaunchedEffect(triggerSubDropdown) {
        if (triggerSubDropdown) {
            expandedDropdown = "sub"
            triggerSubDropdown = false // 다시 꺼줌
        }
    }
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // 내부 아이콘 크기에 맞게 Row 높이 조절
                    .background(Color(0xFFEFD7FF))
                    .padding(vertical = 30.dp), // 약간의 여백만 줄 때
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔙 Back 아이콘
                Box(
                    modifier = Modifier.padding(top = 30.dp) // ⬅️ Back만 위로 올림
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back), // ← 아이콘
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                bottomBarBackClick()
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
// 💡 Idea 아이콘
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
        },
        snackbarHost = {
            CustomSnackbarHost(snackbarHostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1D8FF))
//                .padding(paddingValues) // ✅ 중요
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        expandedDropdown = null // 🔥 드롭다운 닫기
                        patentDropdownExpanded = false // 👈 여기에 이 줄 추가!
                        //                focusManager.clearFocus() // 🔥 포커스도 해제
                        snackbarHostState.currentSnackbarData?.dismiss() // ✅ 스낵바 닫기
                        focusManager.clearFocus() // 🔥 커서 해제
                    })
                }
        ) {
            // ▶ 메인 화면 전체
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // ✅ 이거 빠지면 다 밀려서 안 보임
                    //                .background(Color(0xFFEFD7FF))
                    .padding(
                        start = sidePadding,
                        end = sidePadding,
                        //              top = contentTopPadding,
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                // 2️⃣ 상단 아이콘
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 95.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_idea),
                        contentDescription = "Idea Icon",
                        modifier = Modifier
                            .size(width = 186.8.dp, height = 169.dp)
                            .clickable {
                                navController.navigate("idea_write") {
                                    popUpTo("idea_write") {
                                        inclusive = true
                                    } // 현재 화면을 스택에서 제거 후 재진입
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                WriteCategoryRowWithDropdowns(
                    selectedCategory = selectedCategory,
                    selectedSubCategory = selectedSubCategory,
                    categories = categoryList,
                    subCategories = subCategories[selectedCategory.ifEmpty { "전체" }] ?: emptyList(),
                    expandedDropdown = expandedDropdown,
                    onExpandedDropdownChange = { dropdownKey ->
                        expandedDropdown = dropdownKey
                        patentDropdownExpanded = false // ✅ 특허 드롭다운 닫기
                        snackbarHostState.currentSnackbarData?.dismiss()
                        focusManager.clearFocus() // ✅ 제목/내용 커서 해제
                    },
                    onCategorySelect = { selected ->
                        selectedCategory = if (selected == "전체") "" else selected
                        selectedSubCategory = "" // 2차 초기화
                        triggerSubDropdown = selected != "전체"
                        patentDropdownExpanded = false // 👈 추가
//                        showCategoryError = false
//                        showSubCategoryError = selected.isNotEmpty() // ✅ 핵심!
                    },
                    onSubCategorySelect = { selected ->
                        selectedSubCategory = selected
                        patentDropdownExpanded = false // 👈 추가
                        expandedDropdown = null
//                        showSubCategoryError = false
                    },
                    showCategoryError = hasTriedToSave && selectedCategory.isEmpty(),
                    showSubCategoryError = hasTriedToSave && selectedCategory.isNotEmpty() && selectedSubCategory.isEmpty()
                )

                val spacerHeight by animateDpAsState(
                    targetValue = if (expandedDropdown != null) 20.dp else 0.dp,
                    label = "DropdownSpacing"
                )

                Spacer(modifier = Modifier.height(spacerHeight))

                PatentCategoryRowWithDropdowns(
                    selectedPatent = selectedPatent,
                    expandedDropdown = patentDropdownExpanded,
                    onExpandedDropdownChange = { expanded ->
                        patentDropdownExpanded = expanded // 👈 여기선 의미가 바로 보여서 유지보수에 좋음
                        expandedDropdown = null // ✅ 카테고리 드롭다운 닫기
                        snackbarHostState.currentSnackbarData?.dismiss() // ✅ 스낵바 닫기
                        focusManager.clearFocus() // ✅ 제목/내용 커서 해제
                    },
                    onPatentSelect = {
                        selectedPatent = it
                        patentDropdownExpanded = false
                        expandedDropdown = null // ✅ 카테고리 드롭다운 닫기
                        showPatentError = false
                    },
                    showPatentError = showPatentError
                )

                val patenSpacerHeight by animateDpAsState(
                    targetValue = if (patentDropdownExpanded) 20.dp else 8.dp,
                    label = "PatentDropdownSpacing"
                )

                Spacer(modifier = Modifier.height(patenSpacerHeight))
                //            // ▶ 특허 여부 필터
                //            Row(
                //                modifier = Modifier
                //                    .fillMaxWidth()
                //                    .padding(vertical = 4.dp),
                //                verticalAlignment = Alignment.CenterVertically,
                //                horizontalArrangement = Arrangement.SpaceBetween
                //            ) {
                //                Row(
                //                    verticalAlignment = Alignment.CenterVertically,
                //                    modifier = Modifier.clickable(
                //                        enabled = !categoryExpanded,
                //                        onClick = { patentExpanded = !patentExpanded }
                //                    )
                //                ) {
                //                    Text("특허 여부", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                //                    Spacer(modifier = Modifier.width(4.dp))
                //                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                //
                //                    DropdownMenu(
                //                        expanded = patentExpanded,
                //                        onDismissRequest = { patentExpanded = false }
                //                    ) {
                //                        listOf("있음", "없음").forEach {
                //                            DropdownMenuItem(
                //                                text = { Text(it) },
                //                                onClick = {
                //                                    selectedPatent = it
                //                                    patentExpanded = false
                //                                }
                //                            )
                //                        }
                //                    }
                //                }
                //
                //                if (selectedPatent == "있음" || selectedPatent == "없음") {
                //                    WrCategoryButton(text = selectedPatent, selected = selectedPatent == "있음")
                //                }
                //            }

                //            Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                ) {
                    BasicTextField(
                        value = ideaTitle,
                        onValueChange = {
                            // 🔐 조건: 44자 이하 & 줄바꿈 2줄 이하
                            if (it.text.length <= 60 && it.text.count { c -> c == '\n' } < 2) {
                                ideaTitle = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .onFocusChanged {
                                titleFocused = it.isFocused
                                if (it.isFocused) {
                                    expandedDropdown = null
                                    patentDropdownExpanded = false
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            },
                            cursorBrush = SolidColor(Color.Black),

                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            color = Color.Black
//                            lineHeight = 16.sp
                        ),
                        singleLine = false,
                        maxLines = 2,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
//                                contentAlignment = Alignment.Center// ⬅️ 상하 중앙 + 왼쪽 정렬
                            ) {
                                if (ideaTitle.text.isEmpty() && !titleFocused) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "제목을 작성해 주세요.",
                                            fontSize = 16.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 20.dp), // 커서 위치 조정
                                ) {
                                    innerTextField()
                                }
                            }
                        }
                    )

                    // 🔺 필수 메시지 (상단 우측 고정)
                    if (hasTriedToSave && ideaTitle.text.isBlank()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_warning),
                                contentDescription = "제목 필수 경고",
                                modifier = Modifier.size(14.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "필수",
                                fontSize = 14.sp,
                                fontFamily = poorstoryFont,
                                color = Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                ) {
                    BasicTextField(
                        value = ideaContent,
                        onValueChange = {
                            // 🔐 글자 수 제한 조건 추가
                            if (it.text.length <= 1500) {
                                ideaContent = it
                            }
                            expandedDropdown = null
                            patentDropdownExpanded = false
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .onFocusChanged {
                                contentFocused = it.isFocused
                                if (it.isFocused) {
                                    expandedDropdown = null
                                    patentDropdownExpanded = false
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            color = Color.Black
//                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        singleLine = false,
                        maxLines = 10,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                if (ideaContent.text.isEmpty() && !contentFocused) {

                                    Text(
                                        text = "자신만의 번뜩이는 아이디어를 남겨 주세요.",
                                        fontSize = 16.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 6.dp, top = 10.dp) // 상단 여백 조절
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    // 🔺 필수 메시지 (내용이 비었을 때만 표시)
                    if (hasTriedToSave && ideaContent.text.isBlank()) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_warning),
                                contentDescription = "내용 필수 경고",
                                modifier = Modifier.size(14.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "필수",
                                fontSize = 14.sp,
                                fontFamily = poorstoryFont,
                                color = Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //                    .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Save Idea",
                        modifier = Modifier
                            .size(19.9.dp)
                            .clickable {
                                focusManager.clearFocus() // 🔥 커서 해제
                                expandedDropdown = null
                                patentDropdownExpanded = false
                                snackbarHostState.currentSnackbarData?.dismiss()

                                hasTriedToSave = true // ✅ 저장 시도 여부 표시

                                if (selectedCategory.isEmpty()) {
                                    showCategoryError = true
                                    showSubCategoryError = false
                                } else {
                                    showCategoryError = false
                                    showSubCategoryError = selectedSubCategory.isEmpty()
                                }

                                showPatentError = selectedPatent == "특허 여부"

                                val hasError =
                                    showCategoryError || showSubCategoryError || showPatentError

                                if (hasError) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "모든 필수 항목을 선택해주세요.",
                                            actionLabel = "확인",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } else {
                                    showSaveDialog = true
                                }
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

                                focusManager.clearFocus() // 🔥 커서 해제
                                expandedDropdown = null
                                patentDropdownExpanded = false
                                snackbarHostState.currentSnackbarData?.dismiss()
                            },
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            if (categoryExpanded) {
                Box(
                    modifier = Modifier
                        .absoluteOffset(x = 24.dp, y = 160.dp)
                        .zIndex(1f)
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Column {
                                categoryList.forEach { category ->
                                    Text(
                                        text = category,
                                        fontSize = 14.sp,
                                        fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedCategory == category) Color.Black else Color.DarkGray,
                                        modifier = Modifier
                                            .clickable {
                                                selectedCategory = category
                                                selectedSubCategory = ""
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }

                            if (selectedCategory.isNotBlank()) {
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    subCategories[selectedCategory]?.forEach { subCategory ->
                                        Text(
                                            text = subCategory,
                                            fontSize = 13.sp,
                                            color = if (selectedSubCategory == subCategory) Color.Black else Color.Gray,
                                            modifier = Modifier
                                                .clickable {
                                                    selectedSubCategory = subCategory
                                                    categoryExpanded = false
                                                }
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // ✅ 카테고리 박스 내부 오른쪽 정렬 닫기 버튼
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { categoryExpanded = false }) {
                                Text("닫기")
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                WriteDetailDeleteIdeaDialog(
                    onConfirm = {
                        showDialog = false
                        if (!ideaId.isNullOrBlank()) {
                            dummyDatas.remove(ideaId) // ✅ 삭제 로직 유지됨
                        }
                        navController.popBackStack()
                    },
                    onDismiss = { showDialog = false }
                )
            }
            if (showSaveDialog) {
                SaveIdeaDialog(
                    onConfirmShare = {
                        showSaveDialog = false
                        val newId = "uniqueId_" + System.currentTimeMillis()
                        val newIdea = Idea(
                            title = ideaTitle.text,
                            content = ideaContent.text,
                            author = UserManager.currentUserNickname,
                            userId = UserManager.currentUserId,
                            category = selectedCategory,
                            subCategory = selectedSubCategory,
                            timestamp = System.currentTimeMillis()
                        )
                        dummyDatas[newId] = newIdea
                        myDummyDatas[newId] = newIdea
                        navController.navigate("idea_list?category=$selectedCategory&subCategory=$selectedSubCategory")
                    },
                    onConfirmPrivate = {
                        showSaveDialog = false
                        val newId = "myId_" + System.currentTimeMillis()
                        val newIdea = Idea(
                            title = ideaTitle.text,
                            content = ideaContent.text,
                            author = UserManager.currentUserNickname,
                            userId = UserManager.currentUserId,
                            category = selectedCategory,
                            subCategory = selectedSubCategory,
                            timestamp = System.currentTimeMillis()
                        )
                        myDummyDatas[newId] = newIdea
                        navController.navigate("my_idea?category=$selectedCategory&subCategory=$selectedSubCategory")
                    },
                    onDismiss = { showSaveDialog = false }
                )
            }

//            // ▶ 하단 고정 네비게이션
//            Box(
//                modifier = Modifier
//                    .fillMaxSize(),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight() // 내부 아이콘 크기에 맞게 Row 높이 조절
//                        .background(Color(0xFFF1D8FF))
//                        .padding(vertical = 30.dp), // 약간의 여백만 줄 때
//                    horizontalArrangement = Arrangement.SpaceAround,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // 🔙 Back 아이콘
//                    Box(
//                        modifier = Modifier.padding(top = 30.dp) // ⬅️ Back만 위로 올림
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_back), // ← 아이콘
//                            contentDescription = "Back",
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clickable {
//                                    navController.popBackStack() // IdeaListScreen으로 돌아가기
//                                },
//                            contentScale = ContentScale.Fit
//                        )
//                    }
//                    // 🥚 Egg 아이콘 (중앙)
//                    Box(
//                        modifier = Modifier.padding(top = 0.dp) // 필요 시 조정
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_egg),
//                            contentDescription = "Egg",
//                            modifier = Modifier
//                                .size(40.dp)
//                                .clickable {
//                                    navController.navigate("main")
//                                },
//                            contentScale = ContentScale.Fit
//                        )
//                    }
//                    Box(
//                        modifier = Modifier.padding(top = 30.dp) // ⬅️ Idea는 살짝 위로 조정
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_idea),
//                            contentDescription = "New Idea",
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clickable {
//                                    navController.navigate("idea_write") // ✅ 여기로 이동!
//                                },
//                            contentScale = ContentScale.Fit
//                        )
//                    }
//                }
//            }
        }
    }
}
//@Composable
//fun WrCategoryButton(text: String, selected: Boolean) {
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

@Composable
fun WriteCategoryRowWithDropdowns(
    selectedCategory: String,
    selectedSubCategory: String,
    categories: List<String>,
    subCategories: List<String>,
    expandedDropdown: String?, // ✅ 추가
    onExpandedDropdownChange: (String?) -> Unit, // ✅ 추가
    onCategorySelect: (String) -> Unit,
    onSubCategorySelect: (String) -> Unit,
    showCategoryError: Boolean = false,
    showSubCategoryError: Boolean = false
) {

//    // 👇 WriteCategoryRowWithDropdowns 내부에 추가
//    val showCategory = selectedCategory.isBlank()
//    val showSubCategory = selectedSubCategory.isBlank()

    // "main", "sub", null 중 하나
    Column {
        // 첫 줄: 1차/2차 나란히 배치
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
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
                        text = if (selectedCategory.isEmpty()) "전체" else selectedCategory,
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

            // 🔺 여기가 포인트: 2차 카테고리 오른쪽에 붙는 에러 메시지
            if (showCategoryError) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning), // ← 아이콘
                        contentDescription = "warning",
                        modifier = Modifier
                            .size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "필수",
                        fontSize = 14.sp,
                        fontFamily = poorstoryFont,
                        color = Color.Red, // 짙은 붉은 글자색
    //                    modifier = Modifier
    ////                        .background(Color(0xFFFFE5E5), shape = RoundedCornerShape(6.dp)) // 연한 핑크 배경
    //                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (selectedCategory.isNotEmpty()) {
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onExpandedDropdownChange(if (expandedDropdown == "sub") null else "sub")
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
            // 🔺 여기가 포인트: 2차 카테고리 오른쪽에 붙는 에러 메시지
            if (showSubCategoryError) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning), // ← 아이콘
                        contentDescription = "warning",
                        modifier = Modifier
                            .size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "2차 카테고리 필수",
                        fontSize = 14.sp,
                        fontFamily = poorstoryFont,
                        color = Color.Red, // 짙은 붉은 글자색
                        //                    modifier = Modifier
                        ////                        .background(Color(0xFFFFE5E5), shape = RoundedCornerShape(6.dp)) // 연한 핑크 배경
                        //                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
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
                    val isSelected = (category == "전체" && selectedCategory.isEmpty()) || category == selectedCategory

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

        AnimatedVisibility(
            visible = expandedDropdown == "sub" && subCategories.isNotEmpty()
        ) {
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

@Composable
fun PatentCategoryRowWithDropdowns(
    selectedPatent: String,
    expandedDropdown: Boolean,
    onExpandedDropdownChange: (Boolean) -> Unit,
    onPatentSelect: (String) -> Unit,
    showPatentError: Boolean = false
) {
    Column {
        // 첫 줄: 특허 여부 버튼 + 선택된 결과 박스
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                // ▶ 특허 여부 클릭 가능한 박스
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onExpandedDropdownChange(!expandedDropdown)
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
                            text = "특허 여부",
                            fontSize = 15.sp,
                            fontFamily = poorstoryFont,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                            contentDescription = "특허 드롭다운"
                        )
                    }
                }
            }

            if (showPatentError) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.ic_warning), // ← 아이콘
                        contentDescription = "warning",
                        modifier = Modifier
                            .size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "필수",
                        fontSize = 14.sp,
                        fontFamily = poorstoryFont,
                        color = Color.Red, // 짙은 붉은 글자색
                        //                    modifier = Modifier
                        ////                        .background(Color(0xFFFFE5E5), shape = RoundedCornerShape(6.dp)) // 연한 핑크 배경
                        //                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // ▶ 선택된 값 표시용 박스 (클릭 불가 + 아이콘 없음)
            if (selectedPatent != "특허 여부") {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)),
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
                            text = selectedPatent,
                            fontSize = 15.sp,
                            fontFamily = poorstoryFont,
                            color = Color.Black
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(13.dp))

        // ▶ 드롭다운 리스트
        AnimatedVisibility(visible = expandedDropdown) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(listOf("있음", "없음")) { option ->
                    val isInitial = selectedPatent == "특허 여부"
                    val isSelected = option == selectedPatent && !isInitial

                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color.White else Color(0xFFFFF779).takeIf { !isInitial } ?: Color.White)
                            .clickable {
                                onPatentSelect(option)
                                onExpandedDropdownChange(false)
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
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

@Composable
fun SaveIdeaDialog(
    onConfirmShare: () -> Unit,
    onConfirmPrivate: () -> Unit,
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
                    text = "아이디어 저장",
                    fontSize = 17.sp,
                    fontFamily = poorstoryFont,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 내용
                Text(
                    text = "아이디어를 어떻게 저장하시겠습니까?",
                    fontSize = 13.sp,
                    fontFamily = poorstoryFont,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 버튼 3개 (공유 / 개인 / 취소)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onConfirmShare,
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
                                "공유",
                                fontSize = 12.sp,
                                fontFamily = poorstoryFont
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onConfirmPrivate,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 60.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(16.dp),
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
                                "개인 저장",
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
fun WriteDetailDeleteIdeaDialog(
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
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = Color(0xFFFFF779), // 밝은 경고 노랑
                shape = RoundedCornerShape(14.dp),
//                shadowElevation = 10.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = "알림",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 10.dp)
                        )
                        Text(
                            text = "필수 항목을 모두 선택해주세요!",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                    data.visuals.actionLabel?.let { label ->
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { data.performAction() }) {
                            Text(
                                text = label, // ✅ actionLabel 버튼
                                color = Color.DarkGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    )
}
