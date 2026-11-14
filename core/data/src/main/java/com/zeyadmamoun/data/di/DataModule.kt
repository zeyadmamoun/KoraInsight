package com.zeyadmamoun.data.di

import com.zeyadmamoun.data.repository.LeaguesRepository
import com.zeyadmamoun.data.repository.LeaguesRepositoryImpl
import com.zeyadmamoun.data.repository.MatchDetailsRepository
import com.zeyadmamoun.data.repository.MatchDetailsRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindLeaguesRepository(
        impl: LeaguesRepositoryImpl
    ): LeaguesRepository

    @Binds
    @Singleton
    abstract fun bindMatchDetailsRepository(
        impl: MatchDetailsRepositoryImpl
    ): MatchDetailsRepository
}
