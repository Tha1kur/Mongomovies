package com.mavilon.mongomovies.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.presentation.components.MoviePosterCard
import com.mavilon.mongomovies.presentation.components.PosterPlaceholder
import com.mavilon.mongomovies.ui.theme.Dimensions

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = remember { container.createSearchViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val isRefreshing = uiState.isRefreshing || (uiState.isLoading && uiState.results.isEmpty())
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Large Search Input (Hero Element)
                TextField(
                    value = uiState.query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.PaddingL, vertical = Dimensions.PaddingM),
                    placeholder = { 
                        Text(
                            "What do you want to watch?",
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.search),
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimensions.CornerRadiusL),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(Dimensions.SpaceM))

                // Results Area
                when {
                    uiState.query.isEmpty() -> {
                        EmptySearchState()
                    }
                    uiState.isLoading && uiState.results.isEmpty() -> {
                        LoadingState()
                    }
                    uiState.error != null -> {
                        ErrorState(
                            message = uiState.error ?: "Unknown error",
                            onRetry = { viewModel.refresh() }
                        )
                    }
                    uiState.results.isEmpty() && uiState.query.isNotEmpty() -> {
                        NoResultsState(query = uiState.query)
                    }
                    else -> {
                        SearchResults(
                            movies = uiState.results,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun SearchResults(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = Dimensions.PaddingL, vertical = Dimensions.SpaceM),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingM),
        verticalArrangement = Arrangement.spacedBy(Dimensions.PaddingM)
    ) {
        items(movies) { movie ->
            MoviePosterCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}

@Composable
private fun EmptySearchState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpaceXXL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceM)
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Search for movies",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Type a movie title to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingState() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = Dimensions.PaddingL, vertical = Dimensions.SpaceM),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingM),
        verticalArrangement = Arrangement.spacedBy(Dimensions.PaddingM)
    ) {
        items(6) {
            PosterPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }
    }
}

@Composable
private fun NoResultsState(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpaceXXL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceM)
        ) {
            Text(
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "We couldn't find \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SpaceXXL),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceM)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
