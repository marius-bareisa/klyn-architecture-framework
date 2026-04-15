package com.klynaf.moviestorage.converter

import com.klynaf.core.domain.model.MediaType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MediaTypeConverterTest {

    private val converter = MediaTypeConverter()

    @Test
    fun `GIVEN MediaType Movie WHEN fromMediaType THEN returns movie`() {
        assertEquals("movie", converter.fromMediaType(MediaType.Movie))
    }

    @Test
    fun `GIVEN MediaType TvShow WHEN fromMediaType THEN returns tv`() {
        assertEquals("tv", converter.fromMediaType(MediaType.TvShow))
    }

    @Test
    fun `GIVEN string movie WHEN toMediaType THEN returns MediaType Movie`() {
        assertEquals(MediaType.Movie, converter.toMediaType("movie"))
    }

    @Test
    fun `GIVEN string tv WHEN toMediaType THEN returns MediaType TvShow`() {
        assertEquals(MediaType.TvShow, converter.toMediaType("tv"))
    }

    @Test
    fun `GIVEN unknown string WHEN toMediaType THEN throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> { converter.toMediaType("film") }
    }

    @Test
    fun `GIVEN MediaType Movie WHEN fromMediaType then toMediaType THEN returns original value`() {
        assertEquals(MediaType.Movie, converter.toMediaType(converter.fromMediaType(MediaType.Movie)))
    }

    @Test
    fun `GIVEN MediaType TvShow WHEN fromMediaType then toMediaType THEN returns original value`() {
        assertEquals(MediaType.TvShow, converter.toMediaType(converter.fromMediaType(MediaType.TvShow)))
    }
}
