package com.klynaf.feature.search.presentation

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.TvShow
import com.klynaf.uicore.model.MediaTypeUi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SearchResultUiModelMapperTest {

    private lateinit var mapper: SearchResultUiModelMapper

    @BeforeEach
    fun setUp() {
        mapper = SearchResultUiModelMapper()
    }

    @Test
    fun `map with MovieItem maps correctly`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )
        val movieItem = MediaItem.MovieItem(movie)
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.map(movieItem)

        assertEquals("movie_1", result.uniqueId)
        assertEquals(movie.title, result.title)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals("2010", result.year)
        assertEquals(movie.id, result.mediaId)
        assertEquals("movie", result.mediaTypeRoute)
        assertEquals(MediaTypeUi.Movie, result.mediaTypeUi)
    }

    @Test
    fun `map with TvItem maps correctly`() {
        val tvShow = TvShow(
            id = 1,
            name = "Breaking Bad",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            firstAirDate = "2008-01-20"
        )
        val tvItem = MediaItem.TvItem(tvShow)
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.map(tvItem)

        assertEquals("tv_1", result.uniqueId)
        assertEquals(tvShow.name, result.title)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals("2008", result.year)
        assertEquals(tvShow.id, result.mediaId)
        assertEquals("tv", result.mediaTypeRoute)
        assertEquals(MediaTypeUi.TvShow, result.mediaTypeUi)
    }

    @Test
    fun `map with MovieItem and null posterPath returns null posterUrl`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = null,
            backdropPath = null,
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )
        val movieItem = MediaItem.MovieItem(movie)

        val result = mapper.map(movieItem)

        assertNull(result.posterUrl)
    }

    @Test
    fun `map with MovieItem and short releaseDate returns empty year`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "20"
        )
        val movieItem = MediaItem.MovieItem(movie)

        val result = mapper.map(movieItem)

        assertEquals("", result.year)
    }
}
