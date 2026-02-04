package com.mavilon.mongomovies.data.repository

import com.mavilon.mongomovies.data.local.database.WatchlistDao
import com.mavilon.mongomovies.data.local.database.WatchlistEntity
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchlistRepositoryImpl(
    private val dao: WatchlistDao
) : WatchlistRepository {
    
    override fun getAllWatchlist(): Flow<List<Movie>> {
        return dao.getAllWatchlist().map { entities ->
            entities.map { it.toMovie() }
        }
    }
    
    override suspend fun addToWatchlist(movie: Movie) {
        dao.insertWatchlistItem(movie.toEntity())
    }
    
    override suspend fun removeFromWatchlist(movieId: Int) {
        dao.deleteById(movieId)
    }
    
    override suspend fun isInWatchlist(movieId: Int): Boolean {
        return dao.isInWatchlist(movieId)
    }
    
    override suspend fun clearWatchlist() {
        dao.clearWatchlist()
    }
    
    private fun Movie.toEntity(): WatchlistEntity {
        return WatchlistEntity(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            genreIds = genreIds.joinToString(",")
        )
    }
    
    private fun WatchlistEntity.toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            genreIds = genreIds.split(",").mapNotNull { it.toIntOrNull() }
        )
    }
}
