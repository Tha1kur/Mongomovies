package com.mavilon.mongomovies.presentation.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit
) {
    val viewModel = remember { container.createWatchlistViewModel() }
    val watchlistMovies by viewModel.watchlistMovies.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Watchlist") },
                actions = {
                    if (watchlistMovies.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearWatchlist() }) {
                            Text("Clear All")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (watchlistMovies.isEmpty()) {
            EmptyWatchlistState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchlistMovies, key = { it.id }) { movie ->
                    WatchlistMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) },
                        onRemove = { viewModel.removeFromWatchlist(movie.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchlistMovieCard(
    movie: Movie,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview ?: "No overview",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "⭐ ${movie.voteAverage}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = movie.releaseDate ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from watchlist",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyWatchlistState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📋",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your watchlist is empty",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add movies to your watchlist to watch them later",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
