package com.mavilon.mongomovies.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.mavilon.mongomovies.BuildConfig
import com.mavilon.mongomovies.data.local.database.MovieDatabase
import com.mavilon.mongomovies.data.remote.api.TmdbApi
import com.mavilon.mongomovies.data.repository.MovieRepositoryImpl
import com.mavilon.mongomovies.data.repository.WatchlistRepositoryImpl
import com.mavilon.mongomovies.domain.repository.MovieRepository
import com.mavilon.mongomovies.domain.repository.WatchlistRepository
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
class AppContainer(context: Context) {
    
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                explicitNulls = false
                isLenient = true
                prettyPrint = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 30000
        }
        install(io.ktor.client.plugins.DefaultRequest) {
            url("https://api.themoviedb.org/3/")
            url {
                parameters.append("api_key", BuildConfig.TMDB_API_KEY)
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }
    }

    init {
        val apiKey = BuildConfig.TMDB_API_KEY.trim()
        Log.i("AppContainer", "=== TMDB API KEY DEBUG ===")
        Log.i("AppContainer", "API key present: ${apiKey.isNotEmpty()}")
        Log.i("AppContainer", "API key length: ${apiKey.length}")
        Log.i("AppContainer", "API key first 10 chars: ${apiKey.take(10)}")
        Log.i("AppContainer", "API key value: $apiKey")
        Log.i("AppContainer", "==========================")
    }
    
    private val tmdbApi = TmdbApi(httpClient)
    
    private val database = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        "movie_database"
    ).build()
    
    val movieRepository: MovieRepository = MovieRepositoryImpl(tmdbApi)
    private val watchlistRepository: WatchlistRepository = WatchlistRepositoryImpl(database.watchlistDao())
    
    fun createHomeViewModel() = 
        com.mavilon.mongomovies.presentation.home.HomeViewModel(movieRepository)
    
    fun createSearchViewModel() = 
        com.mavilon.mongomovies.presentation.search.SearchViewModel(movieRepository)
    
    fun createMovieDetailsViewModel(movieId: Int) = 
        com.mavilon.mongomovies.presentation.details.MovieDetailsViewModel(
            movieId = movieId,
            movieRepository = movieRepository,
            watchlistRepository = watchlistRepository
        )
    
    fun createWatchlistViewModel() = 
        com.mavilon.mongomovies.presentation.watchlist.WatchlistViewModel(watchlistRepository)
}
