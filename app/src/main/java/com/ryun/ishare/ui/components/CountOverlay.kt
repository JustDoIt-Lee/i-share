package com.ryun.ishare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.ryun.ishare.R
import com.ryun.ishare.ui.style.CountOverlayDefaults

@Composable
fun CountOverlay(
    count: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = CountOverlayDefaults.FontSize,
    textOffsetY: Dp = CountOverlayDefaults.TextOffsetY,
    iconSize: Dp = CountOverlayDefaults.IconSize
) {
    Box(
        modifier = modifier.size(iconSize),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = "plus",
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = count.toString(),
            fontSize = fontSize,
            color = Color.Black,
            modifier = Modifier.offset(y = textOffsetY)
        )
    }
}