package com.ryun.ishare.ui.style

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier

fun Modifier.menuIcon(onClick: () -> Unit): Modifier =
    this
        .size(CommentItemStyle.IconSize)
        .clickable(onClick = onClick)
