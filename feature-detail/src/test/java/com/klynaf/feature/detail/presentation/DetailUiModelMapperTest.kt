package com.klynaf.feature.detail.presentation

import com.klynaf.core.domain.model.Cast
import com.klynaf.core.domain.model.MediaItem
import com.klynaf.core.domain.model.Movie
import com.klynaf.core.domain.model.Trailer
import com.klynaf.core.domain.model.TvShow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DetailUiModelMapperTest {

    private lateinit var mapper: DetailUiModelMapper

    @BeforeEach
    fun setUp() {
        mapper = DetailUiModelMapper()
    }

    @Test
    fun `mapMovie maps Movie to MovieDetail correctly`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )
        val expectedBackdropUrl = "https://image.tmdb.org/t/p/w780/backdrop.jpg"

        val result = mapper.mapMovie(movie)

        assertTrue(result is DetailUiModel.MovieDetail)
        val movieDetail = result as DetailUiModel.MovieDetail
        assertEquals(movie.title, movieDetail.title)
        assertEquals(movie.posterPath, movieDetail.posterPath)
        assertEquals(movie.overview, movieDetail.overview)
        assertEquals("2010", movieDetail.releaseYear)
        assertEquals("8.8", movieDetail.formattedRating)
        assertEquals(expectedBackdropUrl, movieDetail.backdropUrl)
    }

    @Test
    fun `mapTvShow maps TvShow to TvDetail correctly`() {
        val tvShow = TvShow(
            id = 1,
            name = "Breaking Bad",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 9.5,
            firstAirDate = "2008-01-20"
        )
        val expectedBackdropUrl = "https://image.tmdb.org/t/p/w780/backdrop.jpg"

        val result = mapper.mapTvShow(tvShow)

        assertTrue(result is DetailUiModel.TvDetail)
        val tvDetail = result as DetailUiModel.TvDetail
        assertEquals(tvShow.name, tvDetail.title)
        assertEquals(tvShow.posterPath, tvDetail.posterPath)
        assertEquals(tvShow.overview, tvDetail.overview)
        assertEquals("2008", tvDetail.releaseYear)
        assertEquals("9.5", tvDetail.formattedRating)
        assertEquals(expectedBackdropUrl, tvDetail.backdropUrl)
    }

    @Test
    fun `mapMovie with short release date returns empty year`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "20"
        )

        val result = mapper.mapMovie(movie) as DetailUiModel.MovieDetail

        assertEquals("", result.releaseYear)
    }

    @Test
    fun `mapCast maps Cast to CastUiModel correctly`() {
        val cast = Cast(
            id = 1,
            name = "Leonardo DiCaprio",
            character = "Cobb",
            profilePath = "/leo.jpg"
        )
        val expectedProfileUrl = "https://image.tmdb.org/t/p/w185/leo.jpg"

        val result = mapper.mapCast(cast)

        assertEquals(cast.id, result.id)
        assertEquals(cast.name, result.name)
        assertEquals(cast.character, result.character)
        assertEquals(expectedProfileUrl, result.profileUrl)
    }

    @Test
    fun `mapping handles null image paths correctly`() {
        val movie = Movie(
            id = 1,
            title = "Inception",
            posterPath = null,
            backdropPath = null,
            overview = "Overview",
            voteAverage = 8.8,
            releaseDate = "2010-07-16"
        )

        val result = mapper.mapMovie(movie) as DetailUiModel.MovieDetail

        assertEquals(null, result.backdropUrl)
    }

    @Test
    fun `mapTrailer maps Trailer to TrailerUiModel correctly`() {
        val trailer = Trailer(
            name = "Official Trailer",
            videoUrl = "https://www.youtube.com/watch?v=123",
            thumbnailUrl = "https://img.youtube.com/vi/123/0.jpg",
            type = "Trailer"
        )

        val result = mapper.mapTrailer(trailer)

        assertEquals(trailer.name, result.name)
        assertEquals(trailer.thumbnailUrl, result.thumbnailUrl)
        assertEquals(trailer.videoUrl, result.videoUrl)
    }

    @Test
    fun `mapMediaItem with MovieItem maps correctly`() {
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

        val result = mapper.mapMediaItem(movieItem)

        assertEquals("movie_1", result.uniqueId)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals(movie.title, result.title)
        assertEquals(movie.voteAverage, result.voteAverage)
        assertEquals(movie.id, result.mediaId)
        assertEquals("movie", result.mediaTypeRoute)
    }

    @Test
    fun `mapMediaItem with TvItem maps correctly`() {
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

        val result = mapper.mapMediaItem(tvItem)

        assertEquals("tv_1", result.uniqueId)
        assertEquals(expectedPosterUrl, result.posterUrl)
        assertEquals(tvShow.name, result.title)
        assertEquals(tvShow.voteAverage, result.voteAverage)
        assertEquals(tvShow.id, result.mediaId)
        assertEquals("tv", result.mediaTypeRoute)
    }

    @Test
    fun `mapCast with null profilePath returns null profileUrl`() {
        val cast = Cast(
            id = 1,
            name = "Leonardo DiCaprio",
            character = "Cobb",
            profilePath = null
        )

        val result = mapper.mapCast(cast)

        assertNull(result.profileUrl)
    }
}
