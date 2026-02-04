package com.mavilon.mongomovies.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.mavilon.mongomovies.presentation.components.PosterImage
import com.mavilon.mongomovies.presentation.components.PosterPlaceholder
import com.mavilon.mongomovies.presentation.components.RatingPill

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
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

    val currentMovies = when (selectedCategory) {
        MovieCategory.NOW_PLAYING -> nowPlayingMovies
        MovieCategory.UPCOMING -> upcomingMovies
        MovieCategory.TOP_RATED -> topRatedMovies
        MovieCategory.POPULAR -> popularMovies
    }
    val isRefreshing = currentMovies.loadState.refresh is LoadState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            nowPlayingMovies.refresh()
            currentMovies.refresh()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
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
                            TabRowDefaults.SecondaryIndicator(
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

            when (selectedCategory) {
                MovieCategory.NOW_PLAYING -> movieListContent(nowPlayingMovies, onMovieClick)
                MovieCategory.UPCOMING -> movieListContent(upcomingMovies, onMovieClick)
                MovieCategory.TOP_RATED -> movieListContent(topRatedMovies, onMovieClick)
                MovieCategory.POPULAR -> movieListContent(popularMovies, onMovieClick)
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
        PosterPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )
    }
}

@Composable
private fun HeroMovieCard(movie: Movie, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        val backdropUrl = com.mavilon.mongomovies.util.ImageUrlBuilder.getBackdropUrl(
            movie.backdropPath ?: movie.posterPath
        )
        SubcomposeAsyncImage(
            model = backdropUrl,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                PosterPlaceholder(modifier = Modifier.fillMaxSize())
            },
            error = {
                PosterPlaceholder(modifier = Modifier.fillMaxSize())
            }
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
            Spacer(modifier = Modifier.height(6.dp))
            RatingPill(rating = movie.voteAverage)
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
            .height(170.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            PosterImage(
                posterPath = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(115.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
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
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    RatingPill(rating = movie.voteAverage)
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
            .height(170.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            PosterPlaceholder(
                modifier = Modifier
                    .width(115.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    PosterPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(18.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PosterPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp)
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
