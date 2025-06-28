package com.ryun.ishare.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.ui.theme.poorstoryFont

@Composable
fun CommentButton(
    text: String,
    onClick: () -> Unit,
    isOutlined: Boolean = false,
    colors: ButtonColors,
    border: BorderStroke = BorderStroke(1.dp, Color.Black)
) {
    val modifier = Modifier
        .defaultMinSize(minHeight = 30.dp, minWidth = 60.dp)

    val shape = RoundedCornerShape(16.dp)
    val padding = PaddingValues(horizontal = 8.dp)

    val content: @Composable RowScope.() -> Unit = {
        Text(text, fontSize = 13.sp, fontFamily = poorstoryFont)
    }

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            contentPadding = padding,
            content = content
        )
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            contentPadding = padding,
            content = content
        )
    }
}
