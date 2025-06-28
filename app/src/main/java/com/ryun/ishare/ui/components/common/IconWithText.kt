package com.ryun.ishare.ui.components.common

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.ui.style.CommentItemStyle

@Composable
fun IconWithText(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    text: String,
    fontSize: TextUnit = 11.sp,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(CommentItemStyle.IconSize) // ← 이거 꼭 필요
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor
        )
    }
}
