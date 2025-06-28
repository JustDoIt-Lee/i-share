package com.ryun.ishare.data.repository

import com.ryun.ishare.model.Idea
import com.ryun.ishare.ui.data.dummyDatas

class DefaultIdeaRepository : IdeaRepository {
    private val ideaList = dummyDatas.values.toMutableList()

    override fun getAllIdeas(): List<Idea> = ideaList

    override fun getIdeasByCategory(category: String?, subCategory: String?): List<Idea> {
        return ideaList.filter {
            (category == null || it.category == category) &&
                    (subCategory == null || it.subCategory == subCategory)
        }
    }

    override fun addIdea(idea: Idea) {
        ideaList.add(idea)
    }

    override fun deleteIdea(ideaId: String) {
        ideaList.removeAll { it.id == ideaId }
    }
}

