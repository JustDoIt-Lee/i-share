package com.ryun.ishare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryun.ishare.R
import com.ryun.ishare.ui.data.IdeaConstants
import com.ryun.ishare.ui.theme.BackgroundColor
import com.ryun.ishare.ui.theme.DropdownBackground
import com.ryun.ishare.ui.theme.ideaFont15
import com.ryun.ishare.ui.theme.ideaFont40
import com.ryun.ishare.viewmodel.IdeaListViewModel
import com.ryun.ishare.viewmodel.IdeaListViewState

@Composable
fun IdeaListHeader(
    uiState: IdeaListViewState,
    onExpandedDropdownChange: (String?) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSubCategorySelect: (String) -> Unit,
    onSearchIconClick: () -> Unit,
    onResetClick: () -> Unit,
    viewModel: IdeaListViewModel,
    focusManager: FocusManager
) {
    Spacer(modifier = Modifier.height(84.57.dp))

    Box(modifier = Modifier.size(width = 93.41.dp, height = 65.41.dp)) {
        Text(
            text = "Idea",
            style = ideaFont40,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { onResetClick() }
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    CategoryBoxWithDropdown(
        selectedCategory = uiState.selectedCategory,
        selectedSubCategory = uiState.selectedSubCategory,
        categories = IdeaConstants.categories,
        subCategories = IdeaConstants.subCategories[uiState.selectedCategory] ?: emptyList(),
        expandedDropdown = uiState.expandedDropdown,
        onExpandedDropdownChange = { viewModel.setExpandedDropdown(it) }, // ✅ 이게 핵심
        onCategorySelect = onCategorySelect,
        onSubCategorySelect = onSubCategorySelect
    )

    Spacer(modifier = Modifier.height(16.dp))

    CustomSearchBar(
        value = uiState.searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        modifier = Modifier.width(270.dp),
        onFocusIn = { viewModel.setExpandedDropdown(null) }, // 드롭다운도 VM 방식 유지
        onSearchIconClick = onSearchIconClick,
        focusManager = focusManager // ✅ 여기서 주입
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)) {
        Row(
            modifier = Modifier.clickable {
                focusManager.clearFocus()
                viewModel.setSortExpanded(true)
                onExpandedDropdownChange(null)
            }.padding(vertical = 4.dp),
    //}.padding(horizontal = 8.dp, vertical = 4.dp),

    verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = uiState.selectedSortOption, style = ideaFont15.copy(Color.Black))
            Spacer(modifier = Modifier.width(4.dp))
            Icon(painter = painterResource(id = R.drawable.ic_dropdown_arrow), contentDescription = null)
        }

        DropdownMenu(
            expanded = uiState.sortExpanded,
            onDismissRequest = { viewModel.setSortExpanded(false) },
            modifier = Modifier
                .width(67.dp)
                .background(DropdownBackground)
                .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
        ) {
            listOf("최신순", "전구순", "공유순", "댓글순").forEachIndexed { index, option ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.onSortOptionSelect(option)
                    },
                    text = {
                        Column(modifier = Modifier.offset(x = 3.dp)) {
                            Text(option, style = ideaFont15.copy(fontSize = 14.sp), maxLines = 1)
                            if (index != 3) HorizontalDivider(
                                modifier = Modifier
                                    .width(150.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .offset(x = -3.dp, y = 12.dp),
                                color = Color.Black,
                                thickness = 1.dp
                            )
                        }
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}