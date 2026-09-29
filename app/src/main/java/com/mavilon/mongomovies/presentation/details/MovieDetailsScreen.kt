package com.mavilon.mongomovies.presentation.details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Cast
import com.mavilon.mongomovies.domain.model.Genre
import com.mavilon.mongomovies.domain.model.MovieDetails
import com.mavilon.mongomovies.domain.model.Review
import com.mavilon.mongomovies.domain.model.Video
import com.mavilon.mongomovies.presentation.components.PosterImage
import com.mavilon.mongomovies.presentation.components.PosterPlaceholder
import com.mavilon.mongomovies.presentation.components.RatingPill
import com.mavilon.mongomovies.ui.theme.Dimensions
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
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWatchlist() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.save),
                            contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                            tint = if (isInWatchlist) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
                MovieDetailsContent(
                    state = state,
                    padding = padding,
                    isInWatchlist = isInWatchlist,
                    onToggleWatchlist = { viewModel.toggleWatchlist() }
                )
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
                        androidx.compose.material3.Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(
    state: MovieDetailsUiState.Success,
    padding: PaddingValues,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("About Movie", "Reviews", "Cast")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Backdrop + Poster Hero Section
        MovieHeroSection(
            details = state.details,
            isInWatchlist = isInWatchlist,
            onToggleWatchlist = onToggleWatchlist
        )
        
        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { 
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall
                        ) 
                    }
                )
            }
        }
        
        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimensions.PaddingL),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SectionSpacing)
        ) {
            when (selectedTab) {
                0 -> { // About Movie
                    item {
                        Text(
                            text = state.details.overview ?: "No overview available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
                        )
                    }
                }
                1 -> { // Reviews
                    if (state.reviews.isNotEmpty()) {
                        items(state.reviews.take(5)) { review ->
                            ReviewCard(review = review)
                        }
                    } else {
                        item {
                            Text(
                                text = "No reviews available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                2 -> { // Cast
                    if (state.cast.isNotEmpty()) {
                        items(state.cast.take(10)) { castMember ->
                            CastCard(cast = castMember)
                        }
                    } else {
                        item {
                            Text(
                                text = "No cast information available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieHeroSection(
    details: MovieDetails,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        // Backdrop Image
        val backdropUrl = ImageUrlBuilder.getBackdropUrl(details.backdropPath ?: details.posterPath)
        SubcomposeAsyncImage(
            model = backdropUrl,
            contentDescription = "${details.title} backdrop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) },
            error = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) }
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        // Floating Poster + Info
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimensions.PaddingL),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingL),
            verticalAlignment = Alignment.Bottom
        ) {
            // Floating Poster
            Card(
                modifier = Modifier
                    .width(110.dp)
                    .height(160.dp),
                shape = RoundedCornerShape(Dimensions.CornerRadiusL),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                PosterImage(
                    posterPath = details.posterPath,
                    contentDescription = details.title,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Movie Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = Dimensions.SpaceM),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceS)
            ) {
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                RatingPill(rating = details.voteAverage)
                
                // Metadata Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceM),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.calendarblank),
                            contentDescription = "Year",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.width(14.dp).height(14.dp)
                        )
                        Text(
                            text = details.releaseDate?.take(4) ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.clock),
                            contentDescription = "Duration",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.width(14.dp).height(14.dp)
                        )
                        Text(
                            text = "${details.runtime ?: 0} Minutes",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ticket),
                            contentDescription = "Genre",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.width(14.dp).height(14.dp)
                        )
                        Text(
                            text = details.genres.firstOrNull()?.name ?: "Action",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CornerRadiusM),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.PaddingM),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceS)
        ) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CastCard(cast: Cast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SpaceS),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.PaddingM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp),
            shape = RoundedCornerShape(Dimensions.CornerRadiusM)
        ) {
            PosterImage(
                posterPath = cast.profilePath,
                contentDescription = cast.name,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS)
        ) {
            Text(
                text = cast.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = cast.character ?: "Unknown",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
