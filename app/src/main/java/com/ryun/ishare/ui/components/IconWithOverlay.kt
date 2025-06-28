package com.ryun.ishare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.ryun.ishare.ui.style.IconWithOverlayDefaults

@Composable
fun IconWithOverlay(
    count: Int,
    iconPainter: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit,
    contentDescription: String? = null,
    iconSize: Dp = IconWithOverlayDefaults.IconSize,
    overlayOffsetX: Dp = IconWithOverlayDefaults.OverlayOffsetX,
    overlayOffsetY: Dp = IconWithOverlayDefaults.OverlayOffsetY
) {
    Box(modifier = Modifier.size(iconSize)) {
        Image(
            painter = iconPainter,
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick),
            contentScale = ContentScale.Fit
        )
        if (count > 0) {
            CountOverlay(
                count = count,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = overlayOffsetX, y = overlayOffsetY)
            )
        }
    }
}

