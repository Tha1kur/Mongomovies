package com.mavilon.mongomovies.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit
) {
    val viewModel = remember { container.createHomeViewModel() }
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    val nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems()
    val upcomingMovies = viewModel.upcomingMovies.collectAsLazyPagingItems()
    val topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems()
    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero Section (Always show Now Playing for now)
        item {
            HeroSection(movies = nowPlayingMovies, onMovieClick = onMovieClick)
        }
        
        // Sticky Header: Categories
        stickyHeader {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal])
                        )
                    },
                    divider = {}
                ) {
                    MovieCategory.values().forEach { category ->
                        Tab(
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) },
                            text = { 
                                Text(
                                    text = category.name.replace("_", " "),
                                    style = MaterialTheme.typography.titleSmall
                                ) 
                            }
                        )
                    }
                }
            }
        }
        
        // List Content
        when (selectedCategory) {
            MovieCategory.NOW_PLAYING -> movieListContent(nowPlayingMovies, onMovieClick)
            MovieCategory.UPCOMING -> movieListContent(upcomingMovies, onMovieClick)
            MovieCategory.TOP_RATED -> movieListContent(topRatedMovies, onMovieClick)
            MovieCategory.POPULAR -> movieListContent(popularMovies, onMovieClick)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroSection(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    // Only show if we have items
    if (movies.itemCount > 0) {
        val pagerState = rememberPagerState(pageCount = { minOf(movies.itemCount, 5) })
        
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) { page ->
                movies[page]?.let { movie ->
                    HeroMovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
            
            // Pager Indicator
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(minOf(movies.itemCount, 5)) { iteration ->
                    val color = if (pagerState.currentPage == iteration) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }
    } else if (movies.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun HeroMovieCard(movie: Movie, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath ?: movie.posterPath}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 200f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Featured",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = movie.releaseDate ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

private fun LazyListScope.movieListContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    // Add padding at the top of the list
    item { Spacer(modifier = Modifier.height(16.dp)) }

    when (movies.loadState.refresh) {
        is LoadState.Loading -> {
            items(5) {
                MovieCardShimmer()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        is LoadState.Error -> {
            item {
                ErrorView(
                    message = (movies.loadState.refresh as LoadState.Error).error.message ?: "Unknown error",
                    onRetry = { movies.retry() }
                )
            }
        }
        is LoadState.NotLoading -> {
            if (movies.itemCount == 0) {
                item { EmptyView() }
            } else {
                items(movies.itemCount) { index ->
                    movies[index]?.let { movie ->
                        MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                when (movies.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            Text(
                                text = "Error loading more",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .width(107.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", movie.voteAverage)}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = movie.releaseDate ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieCardShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(107.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Error: $message", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmptyView() {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text("No movies found")
    }
}
