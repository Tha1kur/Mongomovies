package com.mavilon.mongomovies.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WatchlistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
