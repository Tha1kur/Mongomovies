package com.mavilon.mongomovies.presentation.details

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.domain.model.Cast
import com.mavilon.mongomovies.domain.model.Genre
import com.mavilon.mongomovies.domain.model.MovieDetails
import com.mavilon.mongomovies.domain.model.Review
import com.mavilon.mongomovies.domain.model.Video
import com.mavilon.mongomovies.presentation.components.PosterImage
import com.mavilon.mongomovies.presentation.components.PosterPlaceholder
import com.mavilon.mongomovies.presentation.components.RatingPill
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            MovieHeader(
                details = state.details,
                isInWatchlist = isInWatchlist,
                onToggleWatchlist = onToggleWatchlist
            )
        }

        item {
            GenreSection(genres = state.details.genres)
        }

        item {
            OverviewSection(overview = state.details.overview)
        }

        if (state.videos.isNotEmpty()) {
            item {
                TrailersSection(videos = state.videos)
            }
        }

        if (state.cast.isNotEmpty()) {
            item {
                CastSection(cast = state.cast.take(10))
            }
        }

        if (state.reviews.isNotEmpty()) {
            item {
                ReviewsSection(reviews = state.reviews.take(3))
            }
        }
    }
}

@Composable
private fun MovieHeader(
    details: MovieDetails,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
    ) {
        val backdropUrl = ImageUrlBuilder.getBackdropUrl(details.backdropPath ?: details.posterPath)
        SubcomposeAsyncImage(
            model = backdropUrl,
            contentDescription = "${details.title} backdrop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) },
            error = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PosterImage(
                posterPath = details.posterPath,
                contentDescription = details.title,
                modifier = Modifier
                    .width(140.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = details.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                details.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RatingPill(rating = details.voteAverage)
                    Text(
                        text = details.releaseDate ?: "Release TBA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    details.runtime?.let { runtime ->
                        Text(
                            text = "${runtime} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                Text(
                    text = "Status: ${details.status ?: "Unknown"} • Votes: ${details.voteCount}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        FilledTonalIconButton(
            onClick = onToggleWatchlist,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
            )
        ) {
            Icon(
                imageVector = if (isInWatchlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                tint = if (isInWatchlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun OverviewSection(overview: String?) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle(text = "Overview")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = overview ?: "No overview available",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CastSection(cast: List<Cast>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(text = "Cast")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cast) { castMember ->
                CastCard(castMember)
            }
        }
    }
}

@Composable
private fun CastCard(cast: Cast) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            val castUrl = ImageUrlBuilder.getCastUrl(cast.profilePath)
            SubcomposeAsyncImage(
                model = castUrl,
                contentDescription = cast.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentScale = ContentScale.Crop,
                loading = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) },
                error = { PosterPlaceholder(modifier = Modifier.fillMaxSize()) }
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = cast.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = cast.character ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReviewsSection(reviews: List<Review>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(text = "Reviews (${reviews.size})")
        reviews.forEach { review ->
            ReviewCard(review)
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreSection(genres: List<Genre>) {
    if (genres.isEmpty()) return
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "Genres")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                AssistChip(
                    onClick = { },
                    label = { Text(genre.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun TrailersSection(videos: List<Video>) {
    val context = LocalContext.current

    val youtubeVideos = remember(videos) {
        videos
            .filter { it.site.equals("YouTube", ignoreCase = true) && !it.key.isNullOrBlank() }
            .sortedWith(
                compareByDescending<Video> { it.official }
                    .thenBy { it.type ?: "" }
            )
    }

    var selectedKey by remember { mutableStateOf<String?>(youtubeVideos.firstOrNull()?.key) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(text = "Trailers")

        // Thumbnails/list
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(youtubeVideos.take(6)) { video ->
                val key = video.key ?: return@items
                val isSelected = selectedKey == key

                Card(
                    onClick = { selectedKey = key },
                    modifier = Modifier
                        .width(240.dp)
                        .height(96.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = video.name,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = video.type ?: "Trailer",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            if (video.official) {
                                Text(
                                    text = "Official",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inline player (kept on details screen)
        selectedKey?.let { key ->
            InlineYoutubePlayer(
                youtubeKey = key,
                onOpenExternal = {
                    val watchUrl = "https://www.youtube.com/watch?v=$key"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)))
                }
            )
        }
    }
}

@Composable
@SuppressLint("SetJavaScriptEnabled")
private fun InlineYoutubePlayer(
    youtubeKey: String,
    onOpenExternal: () -> Unit
) {
    val embedUrl = "https://www.youtube.com/embed/$youtubeKey?autoplay=1&loop=1&playlist=$youtubeKey&modestbranding=1&rel=0"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        loadUrl(embedUrl)
                    }
                },
                update = { webView ->
                    val current = webView.url
                    if (current == null || !current.contains(youtubeKey)) {
                        webView.loadUrl(embedUrl)
                    }
                }
            )

            TextButton(
                onClick = onOpenExternal,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text("Open")
            }
        }
    }
}
