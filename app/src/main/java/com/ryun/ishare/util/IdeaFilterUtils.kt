package com.ryun.ishare.util

import com.ryun.ishare.model.Idea

fun filterIdeasByCategory(
    ideas: List<Map.Entry<String, Idea>>,
    selectedCategory: String,
    selectedSubCategory: String
): List<Map.Entry<String, Idea>> {
    return ideas.filter { entry ->
        (selectedCategory == "전체" || entry.value.category == selectedCategory) &&
                (selectedSubCategory.isEmpty() || entry.value.subCategory == selectedSubCategory)
    }
}