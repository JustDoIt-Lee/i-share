package com.ryun.ishare.ui.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import com.ryun.ishare.ui.theme.poorstoryFont

object CommentItemStyle {
    val TextColor = Color.Black

    val VerticalPadding = 4.dp
    val NicknameTextStyle = TextStyle(
        fontSize = 12.sp,
        fontFamily = poorstoryFont,
        color = TextColor
    )

    val ReplyNicknameTextStyle = TextStyle(
        fontSize = 10.sp,
        fontFamily = poorstoryFont,
        color = TextColor
    )
    val ContentTextStyle = 13.sp
    val ReplyContentTextStyle = 12.sp

    val ContentFontSize = 12.sp
    val IconSize = 16.dp
    val ContentIconSpacer = 2.dp
    val IconSpacer = 8.dp
    val MenuIconSpacer = 6.dp
    val NicknameBoxPadding = 4.dp
    val NicknameBoxMinHeight = 24.dp
    val ContentCornerShape = 12.dp
}
