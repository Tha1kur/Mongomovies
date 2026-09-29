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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.presentation.components.PosterImage
import com.mavilon.mongomovies.presentation.components.PosterPlaceholder
import com.mavilon.mongomovies.presentation.components.MoviePosterCard
import com.mavilon.mongomovies.ui.theme.Dimensions

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    container: AppContainer,
    onMovieClick: (Int) -> Unit,
    onSearchClick: () -> Unit
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
            contentPadding = PaddingValues(bottom = Dimensions.PaddingL)
        ) {
            // Header Text
            item {
                Text(
                    text = "What do you want to watch?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(
                        start = Dimensions.PaddingL,
                        end = Dimensions.PaddingL,
                        top = Dimensions.PaddingM,
                        bottom = Dimensions.PaddingM
                    )
                )
            }

            // Search Bar (Reduced Height)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.PaddingL)
                        .height(48.dp)
                        .clickable(onClick = onSearchClick),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimensions.PaddingM),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimensions.PaddingM))
            }

            // Featured / Hero Section (Reduced Height)
            item {
                HeroSection(movies = nowPlayingMovies, onMovieClick = onMovieClick)
            }

            // Category Tabs (Reduced Top Spacing)
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedCategory.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = Dimensions.PaddingL,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedCategory.ordinal])
                                    .height(2.dp),
                                color = MaterialTheme.colorScheme.primary
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
                                        text = when (category) {
                                            MovieCategory.NOW_PLAYING -> "Now playing"
                                            MovieCategory.UPCOMING -> "Upcoming"
                                            MovieCategory.TOP_RATED -> "Top rated"
                                            MovieCategory.POPULAR -> "Popular"
                                        },
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Movie Grid Content (Compact)
            when (selectedCategory) {
                MovieCategory.NOW_PLAYING -> movieGridContent(nowPlayingMovies, onMovieClick)
                MovieCategory.UPCOMING -> movieGridContent(upcomingMovies, onMovieClick)
                MovieCategory.TOP_RATED -> movieGridContent(topRatedMovies, onMovieClick)
                MovieCategory.POPULAR -> movieGridContent(popularMovies, onMovieClick)
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
    val heroMovies = remember(movies.itemCount) {
        (0 until minOf(5, movies.itemCount)).mapNotNull { movies[it] }
    }

    if (heroMovies.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { heroMovies.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.PaddingM)
    ) {
        // Hero Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 70.dp),
            pageSpacing = 16.dp
        ) { page ->
            val movie = heroMovies[page]
            FeaturedMovieCard(
                movie = movie,
                ranking = page + 1,
                onClick = { onMovieClick(movie.id) }
            )
        }

        // Page Indicators
        Spacer(modifier = Modifier.height(Dimensions.PaddingM))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(heroMovies.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                )
                if (index < heroMovies.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
private fun FeaturedMovieCard(
    movie: Movie,
    ranking: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(Dimensions.CornerRadiusL),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Poster Image (Full)
            PosterImage(
                posterPath = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize()
            )

            // Ranking Number Overlay (Large, Prominent)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, bottom = 12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = ranking.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 96.sp
                    ),
                    color = Color.Black.copy(alpha = 0.2f)
                )
            }
        }
    }
}

private fun LazyListScope.movieGridContent(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit
) {
    // Calculate number of rows (2 columns per row)
    val itemCount = movies.itemCount
    val rowCount = (itemCount + 1) / 2
    
    // Add movie items as rows
    items(rowCount) { rowIndex ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.PaddingL),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingM)
        ) {
            // First column
            val firstIndex = rowIndex * 2
            if (firstIndex < itemCount) {
                movies[firstIndex]?.let { movie ->
                    Box(modifier = Modifier.weight(1f)) {
                        MoviePosterCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                } ?: Box(modifier = Modifier.weight(1f)) {
                    PosterPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Second column
            val secondIndex = firstIndex + 1
            if (secondIndex < itemCount) {
                movies[secondIndex]?.let { movie ->
                    Box(modifier = Modifier.weight(1f)) {
                        MoviePosterCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                } ?: Box(modifier = Modifier.weight(1f)) {
                    PosterPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        // Add spacing between rows
        if (rowIndex < rowCount - 1) {
            Spacer(modifier = Modifier.height(Dimensions.PaddingM))
        }
    }

    // Loading state
    if (movies.loadState.append is LoadState.Loading) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.PaddingL),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // Error state
    if (movies.loadState.refresh is LoadState.Error) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.PaddingL),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceM)
                ) {
                    Text(
                        text = "Failed to load movies",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { movies.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
