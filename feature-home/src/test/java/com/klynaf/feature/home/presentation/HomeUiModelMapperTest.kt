package com.klynaf.feature.home.presentation

import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HomeUiModelMapperTest {

    private lateinit var mapper: HomeUiModelMapper

    @BeforeEach
    fun setUp() {
        mapper = HomeUiModelMapper()
    }

    @Test
    fun `mapMovie maps Movie to MediaCardUiModel correctly`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.mapMovie(movie)

        assertEquals("movie_1", result.uniqueId)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals(movie.title, result.title)
        assertEquals(movie.voteAverage, result.voteAverage)
        assertEquals(movie.id, result.mediaId)
        assertEquals("movie", result.mediaTypeRoute)
    }

    @Test
    fun `mapMovie with null posterPath returns null posterUrl`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = null,
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )

        val result = mapper.mapMovie(movie)

        assertNull(result.posterUrl)
    }

    @Test
    fun `mapTvShow maps TvShow to MediaCardUiModel correctly`() {
        val tvShow = TvShow(
            id = 1,
            name = "Breaking Bad",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            firstAirDate = "2008-01-20"
        )
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.mapTvShow(tvShow)

        assertEquals("tv_1", result.uniqueId)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals(tvShow.name, result.title)
        assertEquals(tvShow.voteAverage, result.voteAverage)
        assertEquals(tvShow.id, result.mediaId)
        assertEquals("tv", result.mediaTypeRoute)
    }

    @Test
    fun `mapTvShow with null posterPath returns null posterUrl`() {
        val tvShow = TvShow(
            id = 1,
            name = "Breaking Bad",
            posterPath = null,
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            firstAirDate = "2008-01-20"
        )

        val result = mapper.mapTvShow(tvShow)

        assertNull(result.posterUrl)
    }
}
