package com.ryun.ishare.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import com.ryun.ishare.ui.components.common.CategoryBox
import com.ryun.ishare.ui.constants.CategoryDropdownType
import com.ryun.ishare.ui.constants.CategoryDropdownType.DEFAULT_CATEGORY
import com.ryun.ishare.ui.theme.CategoryBackground
import com.ryun.ishare.ui.theme.Dimens

@Composable
fun CategoryBoxWithDropdown(
    selectedCategory: String,
    selectedSubCategory: String,
    categories: List<String>,
    subCategories: List<String>,
    expandedDropdown: String?,
    onExpandedDropdownChange: (String?) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSubCategorySelect: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isMainCategorySelected = selectedCategory != DEFAULT_CATEGORY && selectedCategory.isNotEmpty()

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.RowSpacing)) {
            CategoryBox(
                text = selectedCategory.ifEmpty { DEFAULT_CATEGORY },
                backgroundColor = CategoryBackground,
                onClick = {
                    handleBoxClick(focusManager, expandedDropdown, CategoryDropdownType.MAIN, onExpandedDropdownChange)
                }
            )

            if (isMainCategorySelected) {
                CategoryBox(
                    text = selectedSubCategory.ifEmpty { DEFAULT_CATEGORY },
                    backgroundColor = Color.White,
                    onClick = {
                        handleBoxClick(focusManager, expandedDropdown, CategoryDropdownType.SUB, onExpandedDropdownChange)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.DropdownHeightSpacing))

        CategoryDropdown(
            items = categories,
            selectedItem = selectedCategory,
            onClick = {
                onCategorySelect(it)
                onExpandedDropdownChange(null)
            },
            isVisible = expandedDropdown == CategoryDropdownType.MAIN
        )

        CategoryDropdown(
            items = subCategories,
            selectedItem = selectedSubCategory,
            onClick = {
                onSubCategorySelect(if (it == DEFAULT_CATEGORY) "" else it)
                onExpandedDropdownChange(null)
            },
            isVisible = expandedDropdown == CategoryDropdownType.SUB,
            isSubCategory = true
        )
    }
}

private fun handleBoxClick(
    focusManager: FocusManager,
    currentType: String?,
    targetType: String,
    onExpandedChange: (String?) -> Unit
) {
    focusManager.clearFocus()
    onExpandedChange(if (currentType == targetType) null else targetType)
}
