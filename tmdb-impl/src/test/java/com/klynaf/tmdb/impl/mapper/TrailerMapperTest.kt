package com.klynaf.tmdb.impl.mapper

import com.klynaf.tmdb.api.dto.TrailerDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TrailerMapperTest {

    @Test
    fun `TrailerDto toDomainOrNull with site YouTube maps correctly`() {
        val dto = TrailerDto(
            key = "123",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer"
        )

        val result = dto.toDomainOrNull()

        assertEquals(dto.name, result?.name)
        assertEquals(dto.type, result?.type)
        assertEquals("https://www.youtube.com/watch?v=123", result?.videoUrl)
        assertEquals("https://img.youtube.com/vi/123/mqdefault.jpg", result?.thumbnailUrl)
    }

    @Test
    fun `TrailerDto toDomainOrNull with site youtube lowercase maps correctly`() {
        val dto = TrailerDto(
            key = "123",
            name = "Official Trailer",
            site = "youtube",
            type = "Trailer"
        )

        val result = dto.toDomainOrNull()

        assertEquals("https://www.youtube.com/watch?v=123", result?.videoUrl)
        assertEquals("https://img.youtube.com/vi/123/mqdefault.jpg", result?.thumbnailUrl)
    }

    @Test
    fun `TrailerDto toDomainOrNull with site Vimeo maps correctly`() {
        val dto = TrailerDto(
            key = "456",
            name = "Teaser",
            site = "Vimeo",
            type = "Teaser"
        )

        val result = dto.toDomainOrNull()

        assertEquals(dto.name, result?.name)
        assertEquals(dto.type, result?.type)
        assertEquals("https://vimeo.com/456", result?.videoUrl)
        assertEquals("", result?.thumbnailUrl)
    }

    @Test
    fun `TrailerDto toDomainOrNull with site vimeo lowercase maps correctly`() {
        val dto = TrailerDto(
            key = "456",
            name = "Teaser",
            site = "vimeo",
            type = "Teaser"
        )

        val result = dto.toDomainOrNull()

        assertEquals("https://vimeo.com/456", result?.videoUrl)
        assertEquals("", result?.thumbnailUrl)
    }

    @Test
    fun `TrailerDto toDomainOrNull with unknown site returns null`() {
        val dto = TrailerDto(
            key = "789",
            name = "Trailer",
            site = "DailyMotion",
            type = "Trailer"
        )

        val result = dto.toDomainOrNull()

        assertNull(result)
    }
}
