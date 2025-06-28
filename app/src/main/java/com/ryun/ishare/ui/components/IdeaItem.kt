package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryun.ishare.ui.style.IdeaItemStyle
import com.ryun.ishare.ui.style.ideaItemCard

@Composable
fun IdeaItem(
    header: @Composable () -> Unit,
    body: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = IdeaItemStyle.CardCornerRadius,
        colors = CardDefaults.cardColors(containerColor = IdeaItemStyle.CardColor),
        modifier = modifier.ideaItemCard()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(IdeaItemStyle.CardPadding)
        ) {
            header()
            Spacer(modifier = Modifier.height(IdeaItemStyle.ItemSpacerHeight))
            body()
            Spacer(modifier = Modifier.height(IdeaItemStyle.ItemSpacerHeight))
            footer()
        }
    }
}
