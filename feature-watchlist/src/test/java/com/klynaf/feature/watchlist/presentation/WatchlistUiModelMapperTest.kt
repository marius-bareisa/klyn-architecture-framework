package com.klynaf.feature.watchlist.presentation

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.uicore.model.MediaTypeUi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WatchlistUiModelMapperTest {

    private lateinit var mapper: WatchlistUiModelMapper

    @BeforeEach
    fun setUp() {
        mapper = WatchlistUiModelMapper()
    }

    @Test
    fun `map with Movie WatchlistItem maps correctly`() {
        val item = WatchlistItem(
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = "/poster.jpg",
            addedAt = 123456789L
        )
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.map(item)

        assertEquals(item.mediaId, result.mediaId)
        assertEquals("movie", result.mediaTypeRoute)
        assertEquals(MediaTypeUi.Movie, result.mediaTypeUi)
        assertEquals(item.title, result.title)
        assertEquals(expectedPosterUrl, result.posterUrl)
    }

    @Test
    fun `map with TvShow WatchlistItem maps correctly`() {
        val item = WatchlistItem(
            mediaId = 1,
            mediaType = MediaType.TvShow,
            title = "Breaking Bad",
            posterPath = "/poster.jpg",
            addedAt = 123456789L
        )
        val expectedPosterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg"

        val result = mapper.map(item)

        assertEquals(item.mediaId, result.mediaId)
        assertEquals("tv", result.mediaTypeRoute)
        assertEquals(MediaTypeUi.TvShow, result.mediaTypeUi)
        assertEquals(item.title, result.title)
        assertEquals(expectedPosterUrl, result.posterUrl)
    }

    @Test
    fun `map with WatchlistItem and null posterPath returns null posterUrl`() {
        val item = WatchlistItem(
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = null,
            addedAt = 123456789L
        )

        val result = mapper.map(item)

        assertNull(result.posterUrl)
    }
}
