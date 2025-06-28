package com.ryun.ishare.ui.extensions

import com.ryun.ishare.viewmodel.CommentData

fun CommentData.isReply(): Boolean = text.startsWith("↳")

fun CommentData.replyToIndex(): Int? =
    if (isReply()) text.removePrefix("↳").substringBefore('|').toIntOrNull() else null