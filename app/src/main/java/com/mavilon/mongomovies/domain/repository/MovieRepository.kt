package com.mavilon.mongomovies.domain.repository

import androidx.paging.PagingData
import com.mavilon.mongomovies.domain.model.Cast
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.model.MovieDetails
import com.mavilon.mongomovies.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getNowPlayingMovies(): Flow<PagingData<Movie>>
    fun getUpcomingMovies(): Flow<PagingData<Movie>>
    fun getTopRatedMovies(): Flow<PagingData<Movie>>
    fun getPopularMovies(): Flow<PagingData<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    suspend fun getMovieDetails(id: Int): Result<MovieDetails>
    suspend fun getMovieCredits(id: Int): Result<List<Cast>>
    suspend fun getMovieReviews(id: Int): Result<List<Review>>
}
