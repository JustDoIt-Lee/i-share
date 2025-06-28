// DialogStyle.kt
package com.ryun.ishare.ui.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.ryun.ishare.ui.theme.poorstoryFont

object DialogStyle {
    val ShapeRadius = 18.dp
    val PaddingHorizontal = 32.dp
    val ContainerColor = Color(0xFFFFFBFB)
    val Border = Color.Black

    val TitleText = TextStyle(
        fontSize = 17.sp,
        fontFamily = poorstoryFont,
        color = Color.Black
    )

    val BodyText = TextStyle(
        fontSize = 13.sp,
        fontFamily = poorstoryFont,
        color = Color.DarkGray
    )

    val ButtonText = TextStyle(
        fontSize = 12.sp,
        fontFamily = poorstoryFont
    )
}
