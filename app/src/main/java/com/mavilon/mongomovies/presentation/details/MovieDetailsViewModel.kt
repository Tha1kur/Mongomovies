package com.mavilon.mongomovies.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavilon.mongomovies.domain.model.Cast
import com.mavilon.mongomovies.domain.model.MovieDetails
import com.mavilon.mongomovies.domain.model.Review
import com.mavilon.mongomovies.domain.repository.MovieRepository
import com.mavilon.mongomovies.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()
    
    // Watchlist state
    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()
    
    init {
        loadMovieDetails()
        checkWatchlistStatus()
    }
    
    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            
            // Load all data concurrently
            val detailsResult = movieRepository.getMovieDetails(movieId)
            val creditsResult = movieRepository.getMovieCredits(movieId)
            val reviewsResult = movieRepository.getMovieReviews(movieId)
            
            if (detailsResult.isSuccess && creditsResult.isSuccess && reviewsResult.isSuccess) {
                _uiState.value = MovieDetailsUiState.Success(
                    details = detailsResult.getOrThrow(),
                    cast = creditsResult.getOrThrow(),
                    reviews = reviewsResult.getOrThrow()
                )
            } else {
                val error = detailsResult.exceptionOrNull() 
                    ?: creditsResult.exceptionOrNull() 
                    ?: reviewsResult.exceptionOrNull()
                _uiState.value = MovieDetailsUiState.Error(
                    error?.message ?: "Failed to load movie details"
                )
            }
        }
    }
    
    private fun checkWatchlistStatus() {
        viewModelScope.launch {
            _isInWatchlist.value = watchlistRepository.isInWatchlist(movieId)
        }
    }
    
    fun toggleWatchlist() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is MovieDetailsUiState.Success) {
                if (_isInWatchlist.value) {
                    watchlistRepository.removeFromWatchlist(movieId)
                } else {
                    // Convert MovieDetails to Movie for watchlist
                    val movie = currentState.details.toMovie()
                    watchlistRepository.addToWatchlist(movie)
                }
                _isInWatchlist.value = !_isInWatchlist.value
            }
        }
    }
    
    fun retry() {
        loadMovieDetails()
    }
}

sealed class MovieDetailsUiState {
    object Loading : MovieDetailsUiState()
    data class Success(
        val details: MovieDetails,
        val cast: List<Cast>,
        val reviews: List<Review>
    ) : MovieDetailsUiState()
    data class Error(val message: String) : MovieDetailsUiState()
}

// Extension to convert MovieDetails to Movie
private fun MovieDetails.toMovie() = com.mavilon.mongomovies.domain.model.Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    genreIds = genres.map { it.id }
)
