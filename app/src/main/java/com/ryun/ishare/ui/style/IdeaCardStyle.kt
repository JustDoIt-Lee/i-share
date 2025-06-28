package com.ryun.ishare.ui.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import com.ryun.ishare.ui.theme.poorstoryFont

object IdeaCardStyle {
    val CardHeight = 62.dp
    val CornerRadius = 21.97.dp
    val SpacerBetweenCardAndIcons = 15.dp
    val SpacerBelowIcons = 12.dp
    val IconSpacing = 10.99.dp
    val IconPaddingEnd = 5.4.dp
    val CategoryStartPadding = 18.dp
    val AuthorEndPadding = 18.dp
    val TitleStartPadding = 16.dp
    val TitleEndPadding = 16.dp
    val CategoryFontSize = 13.sp
    val AuthorFontSize = 12.sp
    val TitleFontSize = 15.sp
    val TitleMaxLength = 22
    val CategoryTextStyle = TextStyle(
        fontSize = 13.sp,
        fontFamily = poorstoryFont,
        color = Color.Black
    )

    val AuthorTextStyle = TextStyle(
        fontSize = 12.sp,
        fontFamily = poorstoryFont,
        color = Color.Gray
    )

    val TitleTextStyle = TextStyle(
        fontSize = 15.sp,
        fontFamily = poorstoryFont,
        color = Color.Black
    )
    val CategoryBoxPadding = PaddingValues(start = 18.dp, end = 16.dp, top = 5.dp)
    val AuthorBoxPadding = PaddingValues(start = 50.dp, end = 18.dp, top = 5.dp)
    val TitleRowPadding = PaddingValues(start = 16.dp, end = 16.dp)
}
