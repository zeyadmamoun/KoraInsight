package com.zeyadmamoun.ai.di

import com.zeyadmamoun.ai.repository.AiChatRepository
import com.zeyadmamoun.ai.repository.AiChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAiChatRepository(
        impl: AiChatRepositoryImpl
    ): AiChatRepository
}