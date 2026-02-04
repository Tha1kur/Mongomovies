package com.mavilon.mongomovies.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genreIds: String, // Stored as comma-separated string
    val addedAt: Long = System.currentTimeMillis()
)
