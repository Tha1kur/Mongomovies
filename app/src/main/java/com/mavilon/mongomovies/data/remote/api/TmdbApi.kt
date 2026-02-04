package com.mavilon.mongomovies.data.remote.api

import com.mavilon.mongomovies.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class TmdbApi(private val client: HttpClient) {
    
    private companion object {
        const val BASE_URL = "https://api.themoviedb.org/3"
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private suspend inline fun <reified T> safeApiCall(block: () -> HttpResponse): T {
        val response = block()
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            val error = try {
                json.decodeFromString<TmdbErrorResponse>(errorBody)
            } catch (e: Exception) {
                TmdbErrorResponse(
                    statusCode = response.status.value,
                    statusMessage = "HTTP ${response.status.value}",
                    success = false
                )
            }
            throw TmdbApiException(error.statusMessage, error.statusCode)
        }
        return response.body()
    }
    
    suspend fun getNowPlaying(page: Int = 1): MovieListResponse {
        return safeApiCall {
            client.get("movie/now_playing") {
                parameter("language", "en-US")
                parameter("region", "US")
                parameter("page", page)
            }
        }
    }
    
    suspend fun getUpcoming(page: Int = 1): MovieListResponse {
        return safeApiCall {
            client.get("movie/upcoming") {
                parameter("language", "en-US")
                parameter("region", "US")
                parameter("page", page)
            }
        }
    }
    
    suspend fun getTopRated(page: Int = 1): MovieListResponse {
        return safeApiCall {
            client.get("movie/top_rated") {
                parameter("language", "en-US")
                parameter("region", "US")
                parameter("page", page)
            }
        }
    }
    
    suspend fun getPopular(page: Int = 1): MovieListResponse {
        return safeApiCall {
            client.get("movie/popular") {
                parameter("language", "en-US")
                parameter("region", "US")
                parameter("page", page)
            }
        }
    }
    
    suspend fun getMovieDetails(id: Int): MovieDetailsDto {
        return safeApiCall {
            client.get("movie/$id") {
                parameter("language", "en-US")
            }
        }
    }
    
    suspend fun getMovieCredits(id: Int): CreditsResponse {
        return safeApiCall {
            client.get("movie/$id/credits") {
                parameter("language", "en-US")
            }
        }
    }
    
    suspend fun getMovieReviews(id: Int, page: Int = 1): ReviewsResponse {
        return safeApiCall {
            client.get("movie/$id/reviews") {
                parameter("language", "en-US")
                parameter("page", page)
            }
        }
    }
    
    suspend fun searchMovies(query: String, page: Int = 1): MovieListResponse {
        return safeApiCall {
            client.get("search/movie") {
                parameter("query", query)
                parameter("language", "en-US")
                parameter("include_adult", false)
                parameter("page", page)
            }
        }
    }
}

class TmdbApiException(message: String, val statusCode: Int) : Exception(message)
