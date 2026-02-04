package com.mavilon.mongomovies.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = remember { container.createSearchViewModel() }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Movies") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search for movies...") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            // Search results
            when {
                searchQuery.isEmpty() -> {
                    EmptySearchState()
                }
                searchQuery.length < 2 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Type at least 2 characters to search")
                    }
                }
                else -> {
                    SearchResultsGrid(searchResults, onMovieClick)
                }
            }
        }
    }
}

@Composable
private fun SearchResultsGrid(
    movies: androidx.paging.compose.LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (movies.loadState.refresh) {
            is LoadState.Loading -> {
                items(10) {
                    MovieGridItemShimmer()
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        text = "Error: ${(movies.loadState.refresh as LoadState.Error).error.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is LoadState.NotLoading -> {
                if (movies.itemCount == 0) {
                    item {
                        Text("No results found", modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(movies.itemCount) { index ->
                        movies[index]?.let { movie ->
                            MovieGridItem(movie = movie, onClick = { onMovieClick(movie.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieGridItem(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ ${movie.voteAverage}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MovieGridItemShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Search for your favorite movies",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
