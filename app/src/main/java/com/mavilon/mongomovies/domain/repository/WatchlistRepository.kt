package com.mavilon.mongomovies.domain.repository

import com.mavilon.mongomovies.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlist(): Flow<List<Movie>>
    suspend fun addToWatchlist(movie: Movie)
    suspend fun removeFromWatchlist(movieId: Int)
    suspend fun isInWatchlist(movieId: Int): Boolean
    suspend fun clearWatchlist()
}
