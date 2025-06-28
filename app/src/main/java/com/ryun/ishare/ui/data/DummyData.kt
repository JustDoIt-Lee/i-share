package com.ryun.ishare.ui.data

//import androidx.compose.runtime.remember
import com.ryun.ishare.model.Idea
import com.ryun.ishare.model.User

val dummyDatas = mutableMapOf(
    "uniqueId1" to Idea(
        title = "맛있는 라면 레시피 공유합니다! 🍜",
        content = "진짜 맛있는 라면 끓이는 비법 대공개! 물 조절이 핵심!",
        nickname = "이강륜",
        userId = "user_123", // ✅ 추가
        category = "창업",
        subCategory = "스타트업",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 24 // 하루 전
    ),

    "uniqueId2" to Idea(
        title = "집에서 간단하게 만드는 5분 토스트 아이디어 🍞",
        content = "바쁜 아침에도 뚝딱! 맛있는 토스트 레시피 공개합니다. 식빵, 계란, 치즈만 있으면 끝!",
        author = "박영희",
        userId = "user_999", // ✅ 추가
        category = "일상생활",
        subCategory = "생활용품",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 60 // 1시간 전
    ),

    "uniqueId3" to Idea(
        title = "초보자를 위한 쉬운 파이썬 입문 🐍",
        content = "파이썬 기초 문법부터 실습까지! Hello World부터 시작해볼까요?",
        author = "ADJALKF",
        userId = "user_888", // ✅ 추가
        category = "일상생활",
        subCategory = "가전",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 5 // 5분 전
    ),

    "uniqueId4" to Idea(
        title = "주말에 떠나기 좋은 서울 근교 드라이브 코스 🚗",
        content = "서울 근교 힐링 드라이브 명소 추천! 북한강 따라 떠나는 낭만 여행",
        author = "eklmvl",
        userId = "user_777", // ✅ 추가
        category = "일상생활",
        subCategory = "청소",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 3 // 3분 전
    ),

    "uniqueId5" to Idea(
        title = "나만의 개성을 담은 스마트폰 꾸미기 📱",
        content = "특별한 스마트폰 케이스와 배경화면 꾸미는 방법! 나만의 스타일을 완성해보세요",
        author = "ㅏ아ㅓㅣㄷ",
        userId = "user_666", // ✅ 추가
        category = "창업",
        subCategory = "스타트업",
        timestamp = System.currentTimeMillis() - 1000L * 60 // 1분 전
    ),

    "uniqueId6" to Idea(
        title = "환경 보호를 위한 재활용 아이디어 ♻️",
        content = "분리수거만 잘해도 환경 보호에 큰 도움이 됩니다. 일상 속 재활용 꿀팁!",
        author = "kdjeojio",
        userId = "user_555", // ✅ 추가
        category = "창업",
        subCategory = "비즈니스 아이디어",
        timestamp = System.currentTimeMillis() - 1000L * 30 // 30초 전
    ),

    "uniqueId7" to Idea(
        title = "간단한 옷 정리 노하우 👕",
        content = "옷장 공간 효율적으로 사용하는 정리 방법! 이제 옷 찾느라 시간 낭비하지 마세요.",
        author = "그래들",
        userId = "user_444", // ✅ 추가
        category = "창업",
        subCategory = "투자",
        timestamp = System.currentTimeMillis() - 1000L * 10 // 10초 전
    ),

    "uniqueId8" to Idea(
        title = "성공적인 스타트업 아이템 선정 전략 🚀",
        content = "시장 조사부터 경쟁 분석까지! 차별화된 아이템으로 스타트업 성공 노하우!",
        author = "keljl",
        userId = "user_333", // ✅ 추가
        category = "창업",
        subCategory = "투자",
        timestamp = System.currentTimeMillis() - 1000L * 5 // 5초 전
    ),

    "uniqueId9" to Idea(
        title = "감각적인 UI 디자인 팁이라디랃거지라이자다ㅓ리자너니ㅏ더라지알디자아지라 🎨",
        content = "사용자 경험을 향상시키는 UI 디자인 노하우! 색상, 레이아웃, 타이포그래피 완벽 가이드",
        author = "아ㅓㅣ다ㅓ",
        userId = "user_222", // ✅ 추가
        category = "창업",
        subCategory = "투자",
        timestamp = System.currentTimeMillis() // 지금
    )
)
