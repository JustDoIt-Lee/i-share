package com.ryun.ishare.di

import com.ryun.ishare.data.repository.IdeaRepository
import com.ryun.ishare.data.repository.DefaultIdeaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideIdeaRepository(): IdeaRepository = DefaultIdeaRepository()
}

