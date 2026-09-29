package com.mavilon.mongomovies.presentation.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.presentation.components.PosterImage
import com.mavilon.mongomovies.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit
) {
    val viewModel = remember { container.createWatchlistViewModel() }
    val watchlistMovies by viewModel.watchlistMovies.collectAsState()

    var pendingRemove by remember { mutableStateOf<Movie?>(null) }
    var confirmClearAll by remember { mutableStateOf(false) }

    if (pendingRemove != null) {
        AlertDialog(
            onDismissRequest = { pendingRemove = null },
            title = { Text("Remove from watchlist?") },
            text = { Text(pendingRemove?.title ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingRemove?.let { viewModel.removeFromWatchlist(it.id) }
                        pendingRemove = null
                    }
                ) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemove = null }) { Text("Cancel") }
            }
        )
    }

    if (confirmClearAll) {
        AlertDialog(
            onDismissRequest = { confirmClearAll = false },
            title = { Text("Clear watchlist?") },
            text = { Text("This will remove all saved movies.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearWatchlist()
                        confirmClearAll = false
                    }
                ) { Text("Clear all") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearAll = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watch list") },
                actions = {
                    if (watchlistMovies.isNotEmpty()) {
                        TextButton(onClick = { confirmClearAll = true }) {
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
                contentPadding = PaddingValues(Dimensions.PaddingL),
                verticalArrangement = Arrangement.spacedBy(Dimensions.PaddingL)
            ) {
                items(watchlistMovies, key = { it.id }) { movie ->
                    WatchlistMovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(Dimensions.CornerRadiusM),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.PaddingM),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingM)
        ) {
            // Poster Image
            Card(
                modifier = Modifier
                    .width(90.dp)
                    .height(120.dp),
                shape = RoundedCornerShape(Dimensions.CornerRadiusM)
            ) {
                PosterImage(
                    posterPath = movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Movie Info Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceS)
            ) {
                // Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Rating Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFFC107)
                    )
                }

                // Metadata Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS)
                ) {
                    // Genre
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ticket),
                            contentDescription = "Genre",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Action",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Year
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.calendarblank),
                            contentDescription = "Year",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = movie.releaseDate?.take(4) ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Duration
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.clock),
                            contentDescription = "Duration",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "139 minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
            verticalArrangement = Arrangement.spacedBy(Dimensions.PaddingM)
        ) {
            Text(
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Your watchlist is empty",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Add movies to your watchlist",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
