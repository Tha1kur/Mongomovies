package com.mavilon.mongomovies.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mavilon.mongomovies.data.mapper.toDomain
import com.mavilon.mongomovies.data.remote.api.TmdbApi
import com.mavilon.mongomovies.data.remote.paging.*
import com.mavilon.mongomovies.domain.model.Cast
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.model.MovieDetails
import com.mavilon.mongomovies.domain.model.Review
import com.mavilon.mongomovies.domain.model.Video
import com.mavilon.mongomovies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
    private val api: TmdbApi
) : MovieRepository {
    
    override fun getNowPlayingMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { NowPlayingPagingSource(api) }
        ).flow
    }
    
    override fun getUpcomingMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { UpcomingPagingSource(api) }
        ).flow
    }
    
    override fun getTopRatedMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { TopRatedPagingSource(api) }
        ).flow
    }
    
    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { PopularPagingSource(api) }
        ).flow
    }
    
    override suspend fun searchMovies(query: String, maxPages: Int): Result<List<Movie>> {
        return try {
            val trimmedQuery = query.trim()
            if (trimmedQuery.isBlank()) {
                return Result.success(emptyList())
            }

            val results = mutableListOf<Movie>()
            var page = 1
            var totalPages = maxPages

            while (page <= maxPages && page <= totalPages) {
                val response = api.searchMovies(trimmedQuery, page)
                results += response.results.map { it.toDomain() }
                totalPages = response.totalPages
                if (page >= totalPages) break
                page++
            }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMovieDetails(id: Int): Result<MovieDetails> {
        return try {
            val details = api.getMovieDetails(id).toDomain()
            Result.success(details)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMovieCredits(id: Int): Result<List<Cast>> {
        return try {
            val credits = api.getMovieCredits(id).cast.map { it.toDomain() }
            Result.success(credits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMovieReviews(id: Int): Result<List<Review>> {
        return try {
            val reviews = api.getMovieReviews(id).results.map { it.toDomain() }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieVideos(id: Int): Result<List<Video>> {
        return try {
            val videos = api.getMovieVideos(id).results.map { it.toDomain() }
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
