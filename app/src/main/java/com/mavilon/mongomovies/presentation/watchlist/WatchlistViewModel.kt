package com.mavilon.mongomovies.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    
    // Watchlist movies as StateFlow
    val watchlistMovies: StateFlow<List<Movie>> = watchlistRepository
        .getAllWatchlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(movieId)
        }
    }
    
    fun clearWatchlist() {
        viewModelScope.launch {
            watchlistRepository.clearWatchlist()
        }
    }
}
