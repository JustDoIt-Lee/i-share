package com.ryun.ishare.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.ryun.ishare.ui.constants.CategoryDropdownType.DEFAULT_CATEGORY
import com.ryun.ishare.ui.theme.CategoryBackground
import com.ryun.ishare.ui.theme.Dimens
import com.ryun.ishare.ui.theme.ideaFont15

@Composable
fun CategoryDropdown(
    items: List<String>,
    selectedItem: String,
    onClick: (String) -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    isSubCategory: Boolean = false
) {
    AnimatedVisibility(visible = isVisible) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.DropDownSpacing)
        ) {
            items(items) { item ->
                val isSelected = isItemSelected(item, selectedItem, isSubCategory)
                val backgroundColor = getItemBackgroundColor(isSelected, isSubCategory)

                Box(
                    modifier = Modifier
                        .height(Dimens.DropdownHeight)
                        .clip(RoundedCornerShape(Dimens.CornerShape))
                        .background(backgroundColor)
                        .clickable { onClick(item) }
                        .padding(horizontal = Dimens.DropdownItemPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = ideaFont15
                    )
                }
            }
        }
    }
}

private fun isItemSelected(
    item: String,
    selectedItem: String,
    isSubCategory: Boolean
): Boolean {
    return if (isSubCategory) {
        (item == DEFAULT_CATEGORY && selectedItem.isEmpty()) || (item == selectedItem)
    } else {
        item == selectedItem
    }
}

private fun getItemBackgroundColor(
    isSelected: Boolean,
    isSubCategory: Boolean
): Color {
    return if (isSubCategory) {
        if (isSelected) Color.White else CategoryBackground
    } else {
        if (isSelected) CategoryBackground else Color.White
    }
}
