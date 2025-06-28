package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ryun.ishare.R
import com.ryun.ishare.ui.components.common.DialogActionButton
import com.ryun.ishare.ui.style.DialogStyle

@Composable
fun EditDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(DialogStyle.ShapeRadius),
            color = DialogStyle.ContainerColor,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DialogStyle.PaddingHorizontal)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_title),
                    style = DialogStyle.TitleText
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.edit_body),
                    style = DialogStyle.BodyText
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    DialogActionButton(
                        text = stringResource(R.string.edit_confirm),
                        onClick = onConfirm,
                        containerColor = Color(0xFFEFD7FF),
                        contentColor = Color.Black
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    DialogActionButton(
                        text = stringResource(R.string.edit_cancel),
                        onClick = onDismiss,
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                }
            }
        }
    }
}