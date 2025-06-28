package com.ryun.ishare.ui.style

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

object IdeaItemStyle {
    val CardCornerRadius = RoundedCornerShape(16.dp)
    val CardColor = Color.White
    val CardPadding = 16.dp
    val CardHeight = 120.dp
    val ItemSpacerHeight = 8.dp
}

fun Modifier.ideaItemCard(): Modifier = this
    .fillMaxWidth()
    .height(IdeaItemStyle.CardHeight)

