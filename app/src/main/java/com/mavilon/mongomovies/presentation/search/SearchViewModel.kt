package com.mavilon.mongomovies.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.repository.MovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Search results with debounce and empty query handling
    val searchResults: Flow<PagingData<Movie>> = _searchQuery
        .debounce(500) // Wait 500ms after user stops typing
        .filter { it.isNotBlank() && it.length >= 2 } // Minimum 2 characters
        .distinctUntilChanged()
        .flatMapLatest { query ->
            movieRepository.searchMovies(query)
        }
        .cachedIn(viewModelScope)
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
}
