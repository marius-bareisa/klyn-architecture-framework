package com.klynaf.data.di

import com.klynaf.core.domain.repository.MovieRepository
import com.klynaf.core.domain.repository.SearchRepository
import com.klynaf.core.domain.repository.TvRepository
import com.klynaf.core.domain.repository.WatchlistRepository
import com.klynaf.core.util.TimeProvider
import com.klynaf.data.repository.MovieRepositoryImpl
import com.klynaf.data.repository.SearchRepositoryImpl
import com.klynaf.data.repository.TvRepositoryImpl
import com.klynaf.data.repository.WatchlistRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = TimeProvider { System.currentTimeMillis() }

    @Provides
    @Singleton
    fun provideWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository = impl

    @Provides
    @Singleton
    fun provideSearchRepository(impl: SearchRepositoryImpl): SearchRepository = impl

    @Provides
    @Singleton
    fun provideMovieRepository(impl: MovieRepositoryImpl): MovieRepository = impl

    @Provides
    @Singleton
    fun provideTvRepository(impl: TvRepositoryImpl): TvRepository = impl
}
