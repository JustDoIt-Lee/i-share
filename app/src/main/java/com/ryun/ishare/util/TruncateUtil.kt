package com.ryun.ishare.util

import com.ryun.ishare.ui.style.IdeaCardStyle

fun truncateTitle(title: String, maxLength: Int = IdeaCardStyle.TitleMaxLength): String {
    return if (title.length > maxLength) {
        title.take(maxLength) + "..."
    } else {
        title
    }
}