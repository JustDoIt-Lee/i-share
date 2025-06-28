package com.ryun.ishare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ryun.ishare.R
import com.ryun.ishare.ui.style.SearchBarStyle
import com.ryun.ishare.ui.theme.SearchBarBackground

@Composable
fun CustomSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearchIconClick: (() -> Unit)? = null,
    onFocusIn: (() -> Unit)? = null,
    focusManager: FocusManager
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(SearchBarStyle.CornerRadius)

    Box(
        modifier = modifier
            .height(SearchBarStyle.Height)
            .clip(shape)
            .background(SearchBarBackground)
            .border(1.dp, SearchBarStyle.BorderColor, shape),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = SearchBarStyle.PaddingStart,
                    end = SearchBarStyle.PaddingEnd,
                    top = SearchBarStyle.PaddingVertical,
                    bottom = SearchBarStyle.PaddingVertical
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) {
                            onFocusIn?.invoke()
                        }
                    },
                textStyle = SearchBarStyle.Text,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty() && !isFocused) {
                            Text(
                                text = stringResource(id = R.string.search_placeholder),
                                style = SearchBarStyle.Placeholder,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .offset(y = SearchBarStyle.PlaceholderOffsetY)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(SearchBarStyle.IconSize)
                    .clickable(enabled = onSearchIconClick != null) {
                        onSearchIconClick?.invoke()
                    }
            )
        }
    }
}
