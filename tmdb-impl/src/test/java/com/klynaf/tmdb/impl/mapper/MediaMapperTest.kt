package com.klynaf.tmdb.impl.mapper

import com.klynaf.core.domain.model.MediaItem
import com.klynaf.tmdb.api.dto.CastDto
import com.klynaf.tmdb.api.dto.MovieDto
import com.klynaf.tmdb.api.dto.SearchResultDto
import com.klynaf.tmdb.api.dto.TvShowDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MediaMapperTest {

    @Test
    fun `MovieDto toDomain maps all fields correctly`() {
        val dto = MovieDto(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.title, result.title)
        assertEquals(dto.posterPath, result.posterPath)
        assertEquals(dto.backdropPath, result.backdropPath)
        assertEquals(dto.overview, result.overview)
        assertEquals(dto.voteAverage, result.voteAverage)
        assertEquals(dto.releaseDate, result.releaseDate)
    }

    @Test
    fun `MovieDto toDomain with null posterPath and backdropPath maps correctly`() {
        val dto = MovieDto(
            id = 1,
            title = "Inception",
            posterPath = null,
            backdropPath = null,
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )

        val result = dto.toDomain()

        assertNull(result.posterPath)
        assertNull(result.backdropPath)
    }

    @Test
    fun `TvShowDto toDomain maps all fields correctly`() {
        val dto = TvShowDto(
            id = 1,
            name = "Breaking Bad",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            firstAirDate = "2008-01-20"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.posterPath, result.posterPath)
        assertEquals(dto.backdropPath, result.backdropPath)
        assertEquals(dto.overview, result.overview)
        assertEquals(dto.voteAverage, result.voteAverage)
        assertEquals(dto.firstAirDate, result.firstAirDate)
    }

    @Test
    fun `CastDto toDomain maps all fields correctly`() {
        val dto = CastDto(
            id = 1,
            name = "Leonardo DiCaprio",
            character = "Cobb",
            profilePath = "/leo.jpg"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.character, result.character)
        assertEquals(dto.profilePath, result.profilePath)
    }

    @Test
    fun `CastDto toDomain with null profilePath maps correctly`() {
        val dto = CastDto(
            id = 1,
            name = "Leonardo DiCaprio",
            character = "Cobb",
            profilePath = null
        )

        val result = dto.toDomain()

        assertNull(result.profilePath)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with movie maps correctly`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "movie",
            title = "Inception",
            name = null,
            posterPath = "/poster.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            firstAirDate = null
        )

        val result = dto.toDomainOrNull()

        assertTrue(result is MediaItem.MovieItem)
        val movie = (result as MediaItem.MovieItem).movie
        assertEquals(dto.id, movie.id)
        assertEquals(dto.title, movie.title)
        assertEquals(dto.posterPath, movie.posterPath)
        assertEquals(dto.overview, movie.overview)
        assertEquals(dto.voteAverage, movie.voteAverage)
        assertEquals(dto.releaseDate, movie.releaseDate)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with movie and null title returns null`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "movie",
            title = null,
            name = null,
            posterPath = "/poster.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            firstAirDate = null
        )

        val result = dto.toDomainOrNull()

        assertNull(result)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with tv maps correctly`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "tv",
            title = null,
            name = "Breaking Bad",
            posterPath = "/poster.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            releaseDate = null,
            firstAirDate = "2008-01-20"
        )

        val result = dto.toDomainOrNull()

        assertTrue(result is MediaItem.TvItem)
        val tvShow = (result as MediaItem.TvItem).tvShow
        assertEquals(dto.id, tvShow.id)
        assertEquals(dto.name, tvShow.name)
        assertEquals(dto.posterPath, tvShow.posterPath)
        assertEquals(dto.overview, tvShow.overview)
        assertEquals(dto.voteAverage, tvShow.voteAverage)
        assertEquals(dto.firstAirDate, tvShow.firstAirDate)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with tv and null name returns null`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "tv",
            title = null,
            name = null,
            posterPath = "/poster.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            releaseDate = null,
            firstAirDate = "2008-01-20"
        )

        val result = dto.toDomainOrNull()

        assertNull(result)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with unknown mediaType returns null`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "person",
            title = "Person",
            name = "Person",
            posterPath = "/poster.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16",
            firstAirDate = null
        )

        val result = dto.toDomainOrNull()

        assertNull(result)
    }

    @Test
    fun `SearchResultDto toDomainOrNull with movie and null optional fields maps to defaults`() {
        val dto = SearchResultDto(
            id = 1,
            mediaType = "movie",
            title = "Inception",
            name = null,
            posterPath = "/poster.jpg",
            overview = null,
            voteAverage = null,
            releaseDate = null,
            firstAirDate = null
        )

        val result = dto.toDomainOrNull()

        assertTrue(result is MediaItem.MovieItem)
        val movie = (result as MediaItem.MovieItem).movie
        assertEquals("", movie.overview)
        assertEquals(0.0, movie.voteAverage)
        assertEquals("", movie.releaseDate)
    }
}
