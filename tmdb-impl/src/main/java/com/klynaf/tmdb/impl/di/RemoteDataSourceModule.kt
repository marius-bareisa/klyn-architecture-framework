package com.klynaf.tmdb.impl.di

import com.klynaf.core.domain.source.remote.MovieRemoteDataSource
import com.klynaf.core.domain.source.remote.SearchRemoteDataSource
import com.klynaf.core.domain.source.remote.TvRemoteDataSource
import com.klynaf.tmdb.impl.repository.MovieRemoteDataSourceImpl
import com.klynaf.tmdb.impl.repository.SearchRemoteDataSourceImpl
import com.klynaf.tmdb.impl.repository.TvRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideSearchRemoteDataSource(impl: SearchRemoteDataSourceImpl): SearchRemoteDataSource =
        impl

    @Provides
    @Singleton
    fun provideMovieRemoteDataSource(impl: MovieRemoteDataSourceImpl): MovieRemoteDataSource = impl

    @Provides
    @Singleton
    fun provideTvRemoteDataSource(impl: TvRemoteDataSourceImpl): TvRemoteDataSource = impl
}
