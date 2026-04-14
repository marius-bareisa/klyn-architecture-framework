package com.klynaf.database.impl.mapper

import com.klynaf.core.domain.model.WatchlistItem
import com.klynaf.moviestorage.entity.WatchlistEntity

internal fun WatchlistEntity.toDomain(): WatchlistItem = WatchlistItem(
    mediaId = mediaId,
    mediaType = mediaType,
    title = title,
    posterPath = posterPath,
    addedAt = addedAt,
)

internal fun WatchlistItem.toEntity(): WatchlistEntity = WatchlistEntity(
    mediaId = mediaId,
    mediaType = mediaType,
    title = title,
    posterPath = posterPath,
    addedAt = addedAt,
)
