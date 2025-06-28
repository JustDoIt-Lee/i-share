package com.ryun.ishare.util

import android.content.Context
import android.content.Intent

fun createShareIntent(link: String): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
}

fun shareIdea(context: Context, link: String) {
    val intent = createShareIntent(link)
    val chooser = Intent.createChooser(intent, "공유 방법 선택")
    context.startActivity(chooser)
}
