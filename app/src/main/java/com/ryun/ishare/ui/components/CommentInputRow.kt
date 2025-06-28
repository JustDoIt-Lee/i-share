package com.ryun.ishare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.ryun.ishare.ui.components.common.CommentButton
import com.ryun.ishare.ui.state.CommentInputUIState

private val InputCornerRadius = RoundedCornerShape(4.dp)
private val InputBorderColor = Color.Gray
private val InputBackground = Color.White
private val InputTextColor = Color.Black
private val InputTextStyle = TextStyle(fontSize = 13.sp, color = InputTextColor)

private val PlaceholderTextStyle = TextStyle(fontSize = 12.sp, color = Color.Gray)
private val PlaceholderYOffset = (-1.5).dp

@Composable
fun CommentInputRow(
    ideaId: String,
    inputState: CommentInputUIState,
    commentInputState: MutableState<TextFieldValue>,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester
) {
    val isReplying = inputState.isReplying
    val isEditing = inputState.isEditing
    val isFocused = inputState.isFocused

    val inputBoxModifier = Modifier
        .height(30.dp)
        .background(InputBackground, shape = InputCornerRadius)
        .border(1.dp, InputBorderColor, shape = InputCornerRadius)
        .padding(horizontal = 8.dp, vertical = 4.dp)

    Row(verticalAlignment = Alignment.CenterVertically) {
        CommentButtonGroup(
            isReplying = isReplying,
            isEditing = isEditing,
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = inputBoxModifier.weight(1f)
        ) {
            if (commentInputState.value.text.isEmpty() && !isFocused) {
                Text(
                    text = placeholder,
                    style = PlaceholderTextStyle,
                    modifier = Modifier.align(Alignment.CenterStart).offset(y = PlaceholderYOffset)
                )
            }
            BasicTextField(
                value = commentInputState.value,
                onValueChange = {
                    commentInputState.value = it
                },
                singleLine = true,
                textStyle = InputTextStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .align(Alignment.CenterStart)
                    .onFocusChanged { onFocusChanged(it.isFocused) }
            )
        }
    }
}

@Composable
private fun CommentButtonGroup(
    isReplying: Boolean,
    isEditing: Boolean,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val isReplyOrEdit = isReplying || isEditing
    val (confirmBg, cancelBg) = if (isReplyOrEdit) Color.White to Color(0xFFEFD7FF) else Color(0xFFEFD7FF) to Color.White

    val confirmButtonColor = ButtonDefaults.buttonColors(containerColor = confirmBg, contentColor = Color.Black)
    val cancelButtonColor = ButtonDefaults.buttonColors(containerColor = cancelBg, contentColor = Color.Black)

    val (confirmText, cancelText) = when {
        isReplying -> "답글 등록" to "닫기"
        isEditing -> "수정" to "취소"
        else -> "등록" to "닫기"
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        CommentButton(
            text = confirmText,
            onClick = onConfirmClick,
            isOutlined = false,
            colors = confirmButtonColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        CommentButton(
            text = cancelText,
            onClick = onCancelClick,
            isOutlined = true,
            colors = cancelButtonColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentInputRow() {
    val state = remember { mutableStateOf(TextFieldValue("")) } // ✅ OK

    val inputUIState = CommentInputUIState(
        isReplying = false,
        isEditing = false,
        isFocused = false
    )

    CommentInputRow(
        ideaId = "idea123",
        inputState = inputUIState,
        commentInputState = state,
        onConfirmClick = {},
        onCancelClick = {},
        onFocusChanged = {},
        placeholder = "댓글을 입력하세요",
        focusRequester = FocusRequester()
    )
}