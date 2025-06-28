# 📱 iShare – 아이디어 공유 플랫폼

> 💡 아이디어의 기록부터 공유, 검증, 거래까지 이어주는 창작자 플랫폼

---

### 📌 프로젝트 개요

- **진행 기간**: 2024.04 ~ 현재 (리팩토링 진행 중)
- **역할**: 기획 100% / UIUX 설계 / Android 앱 개발
- **핵심 기술 스택**: Kotlin, Jetpack Compose, ViewModel, Hilt, StateFlow

---

### 🚀 주요 기능

#### ✅ 구현 완료
- 아이디어 등록 / 수정 / 삭제
- 댓글, 공감, 공유, 정렬
- 공개/비공개 설정, 카테고리 필터, 검색 기능

#### ⏳ 구현 예정
- AI 기반 창업 가능성 분석
- NDA 열람 제한 기능
- 회원가입 / 로그인

---

### 🛠 사용 기술

| 분야 | 기술 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose |
| 아키텍처 | MVVM, ViewModel, StateFlow |
| 의존성 주입 | Hilt |
| 협업 도구 | Notion, Figma |
| 버전 관리 | Git, GitHub |

---

### 📁 프로젝트 구조

```bash
app/
├── data/           # Repository, 데이터 접근 계층
├── model/          # 데이터 모델 정의
├── ui/             # UI 컴포저블 및 화면
├── viewmodel/      # ViewModel 계층
├── util/           # 공통 유틸 함수들
└── di/             # Hilt 의존성 주입 설정

---

### 🔄 현재 상태

> ✅ 핵심 기능 중심의 MVP 완성  
> 🛠 구조 리팩토링 및 상태 관리 개선 진행 중  
> 🔜 사용자 인증, AI 분석, NDA 기능 등 차후 개발 예정

---

### 🔍 사용 흐름 예시

앱 진입 → 아이디어 목록 조회 → 상세 화면 → 댓글 or 공감 or 공유
                            ↳ 비공개 설정 / NDA 제한
                            ↳ 향후 거래 or 협업 제안으로 확장 가능

---

### 🙋‍♂️ 기획 및 개발 의도

창업과 기획에 관심이 많아, 사용자 입장에서 꼭 필요한 기능만으로 구성한 실전 MVP를 설계했습니다.

개발자는 아니었지만, Kotlin 및 Compose를 독학하고 전체 개발을 직접 구현했습니다.

기획자로서의 서비스 설계, 사용자 흐름, UI/UX 개선 역량을 보여줄 수 있는 프로젝트입니다.

---

💡 향후 계획

Firebase 연동 및 실 사용자 테스트

아이디어 분석 및 피드백 AI 기능

창작자 간 거래/협업 기능 정식 오픈

---

### 📸 앱 화면 미리보기

<p align="center">
  <img src="https://github.com/JustDoIt-Lee/iShare/blob/main/screenshot/iShare.png?raw=true" width="500"/>
</p>
