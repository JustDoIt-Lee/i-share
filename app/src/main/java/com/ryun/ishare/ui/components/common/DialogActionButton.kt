package com.ryun.ishare.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ryun.ishare.ui.style.DialogStyle

@Composable
fun DialogActionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .defaultMinSize(minWidth = 60.dp)
            .height(30.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        border = BorderStroke(1.dp, DialogStyle.Border)
    ) {
        Text(
            text = text,
            style = DialogStyle.ButtonText
        )
    }
}
