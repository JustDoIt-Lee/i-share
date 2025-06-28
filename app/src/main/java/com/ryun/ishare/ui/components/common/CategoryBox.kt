package com.ryun.ishare.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ryun.ishare.R
import com.ryun.ishare.ui.theme.Dimens.CornerShape
import com.ryun.ishare.ui.theme.Dimens.TextArrowWidthSpacing
import com.ryun.ishare.ui.theme.categoryBoxModifier
import com.ryun.ishare.ui.theme.ideaFont15

@Composable
fun CategoryBox(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(CornerShape),
        color = backgroundColor,
        shadowElevation = 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = categoryBoxModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, style = ideaFont15)
            Spacer(modifier = Modifier.width(TextArrowWidthSpacing))
            Icon(
                painter = painterResource(id = R.drawable.ic_dropdown_arrow),
                contentDescription = "카테고리 드롭다운",
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )
        }
    }
}
