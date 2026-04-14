package com.klynaf.tmdb.impl.mapper

import com.klynaf.core.domain.model.Trailer
import com.klynaf.tmdb.api.dto.TrailerDto

internal fun TrailerDto.toDomainOrNull(): Trailer? {
    val videoUrl: String
    val thumbnailUrl: String
    when (site.lowercase()) {
        "youtube" -> {
            videoUrl = "https://www.youtube.com/watch?v=$key"
            thumbnailUrl = "https://img.youtube.com/vi/$key/mqdefault.jpg"
        }

        "vimeo" -> {
            videoUrl = "https://vimeo.com/$key"
            thumbnailUrl = ""
        }

        else -> return null
    }

    return Trailer(
        name = name,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        type = type
    )
}
