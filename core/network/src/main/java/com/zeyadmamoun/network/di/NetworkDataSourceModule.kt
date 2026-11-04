package com.zeyadmamoun.network.di

import com.zeyadmamoun.network.ktor.KtorNetworkDataSource
import com.zeyadmamoun.network.ktor.NetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkDataSourceModule {

    @Binds
    @Singleton
    fun bindNetworkDataSource(
        impl: KtorNetworkDataSource
    ): NetworkDataSource
}
