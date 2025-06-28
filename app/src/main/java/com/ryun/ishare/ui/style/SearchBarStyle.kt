package com.ryun.ishare.ui.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.ryun.ishare.ui.theme.poorstoryFont

object SearchBarStyle {
    val Height = 32.7.dp
    val CornerRadius = 22.dp
    val PaddingStart = 12.dp
    val PaddingEnd = 13.dp
    val PaddingVertical = 6.dp
    val IconSize = 20.dp
    val FontSize = 13.sp
    val PlaceholderOffsetY = (-1.5).dp
    val BorderColor = Color.Black

    val Text = TextStyle(
        fontSize = FontSize,
        fontFamily = poorstoryFont,
        color = Color.Black
    )

    val Placeholder = TextStyle(
        fontSize = FontSize,
        fontFamily = poorstoryFont,
        color = Color.Gray
    )
}
