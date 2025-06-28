package com.ryun.ishare.data.repository

import com.ryun.ishare.model.Idea

interface IdeaRepository {
    fun getAllIdeas(): List<Idea>
    fun getIdeasByCategory(category: String?, subCategory: String?): List<Idea>
    fun addIdea(idea: Idea)
    fun deleteIdea(ideaId: String)
}
