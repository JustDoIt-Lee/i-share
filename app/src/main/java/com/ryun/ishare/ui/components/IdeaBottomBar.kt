package com.ryun.ishare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ryun.ishare.R
import com.ryun.ishare.ui.theme.BackgroundColor

@Composable
fun IdeaBottomBar(
    onBackClick: () -> Unit,
    onMainClick: () -> Unit,
    onWriteClick: () -> Unit
) {
    val iconSize = 32.dp
    val eggSize = 40.dp
    val bottomBarVerticalPadding = 30.dp
    val iconTopPadding = 30.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(BackgroundColor)
            .padding(vertical = bottomBarVerticalPadding),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔙 Back 아이콘
        Box(
            modifier = Modifier.padding(top = iconTopPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onBackClick() },
                contentScale = ContentScale.Fit
            )
        }
        // 🥚 Egg 아이콘
        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_egg),
                contentDescription = "Egg",
                modifier = Modifier
                    .size(eggSize)
                    .clickable { onMainClick() },
                contentScale = ContentScale.Fit
            )
        }
        // 💡 Idea 아이콘
        Box(
            modifier = Modifier.padding(top = iconTopPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_idea),
                contentDescription = "New Idea",
                modifier = Modifier
                    .size(iconSize)
                    .clickable { onWriteClick() },
                contentScale = ContentScale.Fit
            )
        }
    }
}
