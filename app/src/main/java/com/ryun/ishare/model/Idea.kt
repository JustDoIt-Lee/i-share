package com.ryun.ishare.model

import java.util.UUID

data class Idea(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String,
    val subCategory: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String
)
