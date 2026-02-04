package com.mavilon.mongomovies.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>
    
    @Query("SELECT * FROM watchlist WHERE id = :movieId")
    suspend fun getWatchlistItem(movieId: Int): WatchlistEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    suspend fun isInWatchlist(movieId: Int): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistEntity): Long
    
    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistEntity): Int
    
    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun deleteById(movieId: Int): Int
    
    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist(): Int
}
