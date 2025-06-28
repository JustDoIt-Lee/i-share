package com.ryun.ishare.ui.theme

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

val ideaFont40 = TextStyle(
    fontSize = 40.sp,
    fontFamily = shizuruFont,
    color = Color.Black
)

val ideaFont15 = TextStyle(
    fontSize = 15.sp,
    fontFamily = poorstoryFont,
    color = Color.Black
)

val ideaFont13Gray = TextStyle(
    fontSize = 13.sp,
    fontFamily = poorstoryFont,
    color = Color.Gray
)

val categoryBoxModifier = Modifier
    .defaultMinSize(minHeight = 34.dp)
    .padding(horizontal = 12.dp)
