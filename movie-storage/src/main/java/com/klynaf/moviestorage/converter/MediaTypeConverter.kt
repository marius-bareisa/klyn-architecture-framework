package com.klynaf.moviestorage.converter

import androidx.room.TypeConverter
import com.klynaf.core.domain.model.MediaType

class MediaTypeConverter {
    @TypeConverter
    fun fromMediaType(value: MediaType): String = when (value) {
        MediaType.Movie -> "movie"
        MediaType.TvShow -> "tv"
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType = when (value) {
        "movie" -> MediaType.Movie
        "tv" -> MediaType.TvShow
        else -> throw IllegalArgumentException("Unknown MediaType value: $value")
    }
}
