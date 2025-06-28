package com.ryun.ishare.util

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberScrollStateWithKey(key: Any): ScrollState {
    return remember(key) { ScrollState(0) }
}
