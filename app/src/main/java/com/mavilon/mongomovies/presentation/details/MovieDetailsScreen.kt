package com.mavilon.mongomovies.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items


import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import android.util.Log
import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.util.ImageUrlBuilder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    container: AppContainer,
    onBackClick: () -> Unit
) {
    val viewModel = remember { container.createMovieDetailsViewModel(movieId) }
    val uiState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWatchlist() }) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                            tint = if (isInWatchlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is MovieDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MovieDetailsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Backdrop Image
                    item {
                        val backdropUrl = ImageUrlBuilder.getBackdropUrl(state.details.backdropPath)
                        Log.d("MovieDetails", "Backdrop URL: $backdropUrl")
                        if (backdropUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(backdropUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Movie Backdrop",
                                placeholder = rememberVectorPainter(Icons.Outlined.Image),
                                error = rememberVectorPainter(Icons.Outlined.BrokenImage),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Movie title and info
                    item {
                        Column {
                            Text(
                                text = state.details.title,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Text(
                                    text = "⭐ ${state.details.voteAverage}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = state.details.releaseDate ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${state.details.runtime ?: 0} min",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    // Genres
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.details.genres) { genre ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(genre.name) }
                                )
                            }
                        }
                    }
                    
                    // Overview
                    item {
                        Column {
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.details.overview ?: "No overview available",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    // Cast
                    if (state.cast.isNotEmpty()) {
                        item {
                            Text(
                                text = "Cast",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(state.cast.take(10)) { castMember ->
                                    Card(
                                        modifier = Modifier.width(120.dp)
                                    ) {
                                        Column {
                                            val castUrl = ImageUrlBuilder.getCastUrl(castMember.profilePath)
                                            Log.d("MovieDetails", "Cast URL for ${castMember.name}: $castUrl")
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(castUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = castMember.name,
                                                placeholder = rememberVectorPainter(Icons.Outlined.Image),
                                                error = rememberVectorPainter(Icons.Outlined.BrokenImage),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(160.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = castMember.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = castMember.character ?: "",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Reviews
                    if (state.reviews.isNotEmpty()) {
                        item {
                            Text(
                                text = "Reviews (${state.reviews.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(state.reviews.take(3)) { review ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = review.author,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = review.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is MovieDetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
