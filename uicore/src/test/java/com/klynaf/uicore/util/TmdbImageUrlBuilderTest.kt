package com.klynaf.uicore.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class TmdbImageUrlBuilderTest {

    @Test
    fun `GIVEN null path WHEN toTmdbImageUrl THEN returns null`() {
        val path: String? = null

        val result = path.toTmdbImageUrl()

        assertNull(result)
    }

    @Test
    fun `GIVEN non-null path WHEN toTmdbImageUrl with W185 THEN returns correct url`() {
        val path = "/profile.jpg"
        val size = ImageSize.W185

        val result = path.toTmdbImageUrl(size)

        assertEquals("https://image.tmdb.org/t/p/w185/profile.jpg", result)
    }

    @Test
    fun `GIVEN non-null path WHEN toTmdbImageUrl with W342 THEN returns correct url`() {
        val path = "/poster.jpg"
        val size = ImageSize.W342

        val result = path.toTmdbImageUrl(size)

        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", result)
    }

    @Test
    fun `GIVEN non-null path WHEN toTmdbImageUrl with W780 THEN returns correct url`() {
        val path = "/backdrop.jpg"
        val size = ImageSize.W780

        val result = path.toTmdbImageUrl(size)

        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", result)
    }

    @Test
    fun `GIVEN non-null path WHEN toTmdbImageUrl with no size argument THEN defaults to W342`() {
        val path = "/poster.jpg"

        val result = path.toTmdbImageUrl()

        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", result)
    }

    @Test
    fun `GIVEN path without leading slash WHEN toTmdbImageUrl THEN concatenates without inserting slash`() {
        val path = "poster.jpg"
        val size = ImageSize.W342

        val result = path.toTmdbImageUrl(size)

        assertEquals("https://image.tmdb.org/t/p/w342poster.jpg", result)
    }
}
