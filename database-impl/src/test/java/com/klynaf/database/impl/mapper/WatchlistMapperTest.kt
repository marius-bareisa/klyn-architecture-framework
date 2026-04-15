package com.klynaf.database.impl.mapper

import com.klynaf.core.domain.model.MediaType
import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.moviestorage.entity.WatchlistEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class WatchlistMapperTest {

    @Test
    fun `toDomain maps WatchlistEntity to WatchlistItem correctly`() {
        val entity = WatchlistEntity(
            id = 0,
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = "/poster.jpg",
            addedAt = 123456789L
        )

        val result = entity.toDomain()

        assertEquals(entity.mediaId, result.mediaId)
        assertEquals(entity.mediaType, result.mediaType)
        assertEquals(entity.title, result.title)
        assertEquals(entity.posterPath, result.posterPath)
        assertEquals(entity.addedAt, result.addedAt)
    }

    @Test
    fun `toDomain with null posterPath returns WatchlistItem with null posterPath`() {
        val entity = WatchlistEntity(
            id = 0,
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = null,
            addedAt = 123456789L
        )

        val result = entity.toDomain()

        assertNull(result.posterPath)
    }

    @Test
    fun `toEntity maps WatchlistItem to WatchlistEntity correctly`() {
        val domain = WatchlistItem(
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = "/poster.jpg",
            addedAt = 123456789L
        )

        val result = domain.toEntity()

        assertEquals(0, result.id)
        assertEquals(domain.mediaId, result.mediaId)
        assertEquals(domain.mediaType, result.mediaType)
        assertEquals(domain.title, result.title)
        assertEquals(domain.posterPath, result.posterPath)
        assertEquals(domain.addedAt, result.addedAt)
    }

    @Test
    fun `toEntity with null posterPath returns WatchlistEntity with null posterPath`() {
        val domain = WatchlistItem(
            mediaId = 1,
            mediaType = MediaType.Movie,
            title = "Inception",
            posterPath = null,
            addedAt = 123456789L
        )

        val result = domain.toEntity()

        assertNull(result.posterPath)
    }
}
