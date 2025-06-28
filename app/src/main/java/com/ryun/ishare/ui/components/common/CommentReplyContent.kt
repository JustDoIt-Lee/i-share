package com.ryun.ishare.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.ui.model.CommentUIModel
import com.ryun.ishare.ui.model.isMenuExpanded
import com.ryun.ishare.ui.state.CommentMenuState
import com.ryun.ishare.ui.style.CommentItemStyle.ContentCornerShape
import com.ryun.ishare.ui.style.CommentItemStyle.ContentTextStyle
import com.ryun.ishare.ui.style.CommentItemStyle.IconSize
import com.ryun.ishare.ui.style.CommentItemStyle.NicknameTextStyle
import com.ryun.ishare.ui.style.CommentItemStyle.ReplyContentTextStyle
import com.ryun.ishare.ui.style.CommentItemStyle.ReplyNicknameTextStyle
import com.ryun.ishare.ui.theme.NicknameBackground

@Composable
fun CommentReplyContent(
    model: CommentUIModel,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onReplyClick: (() -> Unit)? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTimeToggle: () -> Unit,
    onMenuStateChange: (CommentMenuState) -> Unit,
    clearAllMenus: () -> Unit,
    clearInputTextOnly: () -> Unit,
    formatRelativeTime: (Long) -> String
) {
    val isMyComment = currentUserId == model.authorId
    val isExpanded = model.isMenuExpanded
    var showTimeOnly by remember { mutableStateOf(false) }

    val formattedTime = formatRelativeTime(model.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (model.isReply) 4.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 본문
        Row(modifier = Modifier.weight(1f)) {
            model.prefix?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF555555),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (model.isReply) Color.White else NicknameBackground,
                        shape = RoundedCornerShape(ContentCornerShape)
                    )
                    .padding(horizontal = if (model.isReply) 4.dp else 10.dp)
                    .defaultMinSize(minHeight = if (model.isReply) 24.dp else 32.dp)
            ) {
                Text(
                    text = model.nickname,
                    style = if (model.isReply) ReplyNicknameTextStyle else NicknameTextStyle,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = model.content,
                fontSize = if (model.isReply) ReplyContentTextStyle else ContentTextStyle,
                overflow = TextOverflow.Clip,
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 아이콘
        Row(verticalAlignment = Alignment.CenterVertically) {
            ActionIcons(
                isMyComment = isMyComment,
                isReply = model.isReply,
                isExpanded = isExpanded,
                showTimeOnly = showTimeOnly,
                likeCount = model.likeCount,
                replyCount = model.replyCount,
                formattedTime = formattedTime,
                onLikeClick = {
                    clearAllMenus()
                    onLikeClick()
                },
                onReplyClick = {
                    onReplyClick?.invoke()
                    clearAllMenus()
                    clearInputTextOnly()
                },
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onTimeToggle = {
                    onTimeToggle()
                    showTimeOnly = true
                },
                onMenuClick = {
                    onMenuStateChange(model.menuState)
                    showTimeOnly = false
                }
            )
        }
    }
}

@Composable
private fun ActionIcons(
    isMyComment: Boolean,
    isReply: Boolean,
    isExpanded: Boolean,
    showTimeOnly: Boolean,
    likeCount: Int,
    replyCount: Int,
    formattedTime: String,
    onLikeClick: () -> Unit,
    onReplyClick: (() -> Unit)?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTimeToggle: () -> Unit,
    onMenuClick: () -> Unit
) {
    if (showTimeOnly) {
        TimeIcon(formattedTime)
        return
    }

    if (isMyComment) {
        if (!isExpanded) {
            LikeReplyIcon(isReply, likeCount, replyCount, onLikeClick, onReplyClick)
        }

        ActionIcon(Icons.Default.Menu, "메뉴", Color.Black, onMenuClick)

        if (isExpanded) {
            Spacer(modifier = Modifier.width(6.dp))

            ActionIcon(Icons.Outlined.AccessTime, "시간", Color.Gray, onTimeToggle)

            Spacer(modifier = Modifier.width(6.dp))

            ActionIcon(Icons.Default.Edit, "수정", Color.Gray, onEditClick)

            Spacer(modifier = Modifier.width(6.dp))

            ActionIcon(Icons.Outlined.Delete, "삭제", Color.Gray, onDeleteClick)

        }
    } else {
        LikeReplyIcon(isReply, likeCount, replyCount, onLikeClick, onReplyClick)

        Spacer(modifier = Modifier.width(8.dp))

        ActionIcon(Icons.Outlined.AccessTime, "시간", Color.Gray, onTimeToggle)

    }
}

@Composable
private fun ActionIcon(
    imageVector: ImageVector,
    description: String,
    tint: Color,
    onClick: () -> Unit
) {
    Icon(
        imageVector = imageVector,
        contentDescription = description,
        tint = tint,
        modifier = Modifier
            .size(IconSize)
            .clickable { onClick() }
    )
}

@Composable
private fun TimeIcon(
    formattedTime: String,
    onClick: () -> Unit = {}
) {
    IconWithText(
        icon = Icons.Outlined.AccessTime,
        contentDescription = "시간",
        tint = Color.Gray,
        text = formattedTime,
        onClick = onClick
    )
}

@Composable
private fun LikeReplyIcon(
    isReply: Boolean,
    likeCount: Int,
    replyCount: Int,
    onLikeClick: () -> Unit,
    onReplyClick: (() -> Unit)?
) {
    IconWithText(
        icon = Icons.Filled.Favorite,
        contentDescription = "좋아요",
        tint = Color.Red,
        text = likeCount.toString(),
        onClick = onLikeClick
    )
    if (!isReply) {
        Spacer(modifier = Modifier.width(8.dp))
        IconWithText(
            icon = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "답글",
            tint = Color.Black,
            text = replyCount.toString(),
            onClick = { onReplyClick?.invoke() }
        )
    }
    Spacer(modifier = Modifier.width(8.dp))
}
