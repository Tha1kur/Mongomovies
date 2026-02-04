package com.mavilon.mongomovies.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    // Selected category
    private val _selectedCategory = MutableStateFlow(MovieCategory.NOW_PLAYING)
    val selectedCategory: StateFlow<MovieCategory> = _selectedCategory.asStateFlow()
    
    // Paging flows for each category - cached in viewModelScope
    val nowPlayingMovies: Flow<PagingData<Movie>> = movieRepository
        .getNowPlayingMovies()
        .cachedIn(viewModelScope)
    
    val upcomingMovies: Flow<PagingData<Movie>> = movieRepository
        .getUpcomingMovies()
        .cachedIn(viewModelScope)
    
    val topRatedMovies: Flow<PagingData<Movie>> = movieRepository
        .getTopRatedMovies()
        .cachedIn(viewModelScope)
    
    val popularMovies: Flow<PagingData<Movie>> = movieRepository
        .getPopularMovies()
        .cachedIn(viewModelScope)
    
    fun selectCategory(category: MovieCategory) {
        _selectedCategory.value = category
    }
}

enum class MovieCategory {
    NOW_PLAYING,
    UPCOMING,
    TOP_RATED,
    POPULAR
}
