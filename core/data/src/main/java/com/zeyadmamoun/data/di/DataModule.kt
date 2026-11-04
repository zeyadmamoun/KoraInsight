package com.zeyadmamoun.data.di

import com.zeyadmamoun.data.repository.MatchRepositoryImpl
import com.zeyadmamoun.data.repository.MatchesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMatchesRepository(
        impl: MatchRepositoryImpl
    ): MatchesRepository
}
