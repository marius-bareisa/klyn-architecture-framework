package com.klynaf.database.impl.di

import com.klynaf.core.domain.source.local.WatchlistLocalDataSource
import com.klynaf.database.impl.source.WatchlistLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideWatchlistLocalDataSource(impl: WatchlistLocalDataSourceImpl): WatchlistLocalDataSource =
        impl
}
