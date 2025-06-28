package com.ryun.ishare.ui.data

// 데이터 클래스
data class CurvedChar(
    val char: Char,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotation: Float
)

// 리스트
val curvedCharList = listOf(
    CurvedChar('I', 72f, 426.6f, 44.1f, 40.3f, -51.01f),
    CurvedChar('d', 90.14f, 409.4f, 39.3f, 42.5f, -40.46f),
    CurvedChar('e', 110f, 395.8f, 34.7f, 44.7f, -30.41f),
    CurvedChar('a', 132.5f, 385.1f, 31.4f, 46.5f, -19.6f),
    CurvedChar('S', 197.3f, 380.3f, 23.7f, 45.2f, 9.3f),
    CurvedChar('h', 216.6f, 385.8f, 30.9f, 46.2f, 20.36f),
    CurvedChar('a', 234.8f, 396.5f, 37.9f, 46f, 31.66f),
    CurvedChar('r', 252.1f, 412.4f, 38.4f, 40.7f, 41.97f),
    CurvedChar('e', 263.8f, 428.3f, 43f, 38.5f, 51.52f),
    CurvedChar('M', 115.4f, 515.2f, 44.9f, 52.7f, 50.66f),
    CurvedChar('y', 135.1f, 530.7f, 29.4f, 49.6f, 20.24f),
    CurvedChar('I', 180.3f, 545.2f, 21.1f, 47.8f, -5.41f),
    CurvedChar('d', 195.5f, 540.3f, 25.4f, 49.3f, -40.47f),
    CurvedChar('e', 205.8f, 525.9f, 33.2f, 49.5f, -50.82f),
    CurvedChar('a', 220.2f, 510.5f, 40f, 50.2f, -50.58f)
)