package com.ryun.ishare.ui.state

data class CommentInputUIState(
    val isReplying: Boolean = false,
    val isEditing: Boolean = false,
    val isFocused: Boolean = false
)
