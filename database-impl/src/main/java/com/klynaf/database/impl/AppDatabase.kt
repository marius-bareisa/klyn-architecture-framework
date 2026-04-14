package com.klynaf.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.klynaf.moviestorage.converter.MediaTypeConverter
import com.klynaf.moviestorage.dao.WatchlistDao
import com.klynaf.moviestorage.entity.WatchlistEntity

@Database(entities = [WatchlistEntity::class], version = 1, exportSchema = false)
@TypeConverters(MediaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
