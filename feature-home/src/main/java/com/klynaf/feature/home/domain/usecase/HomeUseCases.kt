package com.klynaf.feature.home.domain.usecase

import javax.inject.Inject

data class HomeUseCases @Inject constructor(
    val getTrendingMovies: GetTrendingMoviesUseCase,
    val getPopularMovies: GetPopularMoviesUseCase,
    val getPopularTv: GetPopularTvUseCase,
    val getTopRatedMovies: GetTopRatedMoviesUseCase,
    val getTopRatedTv: GetTopRatedTvUseCase,
)
