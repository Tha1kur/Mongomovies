package com.mavilon.mongomovies.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.domain.repository.MovieRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var rawResults: List<Movie> = emptyList()
    private val resultsCache = mutableMapOf<String, List<Movie>>()

    init {
        observeQuery()
    }

    private fun observeQuery() {
        viewModelScope.launch {
            uiState.map { it.query }
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.length < 2) {
                        rawResults = emptyList()
                        _uiState.value = _uiState.value.copy(
                            results = emptyList(),
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    } else {
                        performSearch(query, isRefresh = false)
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun clearSearch() {
        rawResults = emptyList()
        _uiState.value = SearchUiState()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            sortOption = SortOption.RELEVANCE,
            yearFilter = "",
            minRating = 0f
        )
        applyFilters()
    }

    fun updateSortOption(option: SortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
        applyFilters()
    }

    fun updateYearFilter(year: String) {
        _uiState.value = _uiState.value.copy(yearFilter = year)
        applyFilters()
    }

    fun updateMinRating(minRating: Float) {
        _uiState.value = _uiState.value.copy(minRating = minRating)
        applyFilters()
    }

    fun refresh() {
        val currentQuery = _uiState.value.query
        if (currentQuery.length >= 2) {
            viewModelScope.launch {
                performSearch(currentQuery, isRefresh = true)
            }
        }
    }

    private suspend fun performSearch(query: String, isRefresh: Boolean) {
        val trimmed = query.trim()
        if (!isRefresh) {
            resultsCache[trimmed]?.let { cached ->
                rawResults = cached
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = null
                )
                applyFilters()
                return
            }
        }

        _uiState.value = _uiState.value.copy(
            isLoading = !isRefresh,
            isRefreshing = isRefresh,
            error = null
        )

        val result = movieRepository.searchMovies(trimmed)
        if (result.isSuccess) {
            rawResults = result.getOrDefault(emptyList())
            resultsCache[trimmed] = rawResults
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                error = null
            )
            applyFilters()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                error = result.exceptionOrNull()?.message ?: "Unknown error"
            )
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val minVote = state.minRating
        val targetYear = state.yearFilter.toIntOrNull()

        val filtered = rawResults.filter { movie ->
            val matchesRating = movie.voteAverage >= minVote
            val matchesYear = targetYear?.let { year ->
                extractYear(movie.releaseDate) == year
            } ?: true
            matchesRating && matchesYear
        }

        val sorted = when (state.sortOption) {
            SortOption.RELEVANCE -> filtered
            SortOption.RATING -> filtered.sortedByDescending { it.voteAverage }
            SortOption.NEWEST -> filtered.sortedWith(
                compareByDescending<Movie> { extractDate(movieReleaseDate = it.releaseDate) ?: LocalDate.MIN }
            )
        }

        _uiState.value = state.copy(results = sorted)
    }

    private fun extractYear(dateString: String?): Int? {
        return dateString?.takeIf { it.length >= 4 }?.substring(0, 4)?.toIntOrNull()
    }

    private fun extractDate(movieReleaseDate: String?): LocalDate? {
        if (movieReleaseDate.isNullOrBlank()) return null
        return try {
            LocalDate.parse(movieReleaseDate, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val results: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val sortOption: SortOption = SortOption.RELEVANCE,
    val yearFilter: String = "",
    val minRating: Float = 0f
)

enum class SortOption(val label: String) {
    RELEVANCE("Default"),
    RATING("Rating"),
    NEWEST("Newest")
}
