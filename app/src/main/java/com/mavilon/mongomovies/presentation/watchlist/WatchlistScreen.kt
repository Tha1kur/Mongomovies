package com.mavilon.mongomovies.presentation.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.presentation.components.MoviePosterCard
import com.mavilon.mongomovies.presentation.components.RatingPill

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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchlistMovies, key = { it.id }) { movie ->
                    WatchlistMovieGridCard(
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
private fun WatchlistMovieGridCard(
    movie: Movie,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        MoviePosterCard(
            movie = movie,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.68f)
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            tonalElevation = 2.dp
        ) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove"
                )
            }
        }

        // Quick glance rating at bottom-start
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingPill(rating = movie.voteAverage)
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
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No saved films yet",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Find a movie you love and tap the heart to save it for later.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
