package com.mavilon.mongomovies.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.domain.model.Movie
import com.mavilon.mongomovies.ui.theme.Dimensions
import com.mavilon.mongomovies.util.ImageUrlBuilder

@Composable
fun RatingPill(
    rating: Double?,
    modifier: Modifier = Modifier
) {
    val formattedRating = rating?.takeIf { it > 0 }?.let { String.format("%.1f", it) } ?: "N/A"
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = Dimensions.SpaceM, vertical = Dimensions.SpaceXS + 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpaceXS + 2.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.star),
            contentDescription = "Rating",
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .clip(CircleShape)
        )
        Text(
            text = formattedRating,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun PosterPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Dimensions.CornerRadiusM)
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(10f, 10f),
        end = Offset(translateAnim, translateAnim)
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun PosterImage(
    posterPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Dimensions.CornerRadiusM),
    contentScale: ContentScale = ContentScale.Crop
) {
    val posterUrl = ImageUrlBuilder.getPosterUrl(posterPath)
    SubcomposeAsyncImage(
        model = posterUrl,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale,
        loading = {
            PosterPlaceholder(modifier = Modifier
                .fillMaxSize()
                .clip(shape))
        },
        error = {
            PosterPlaceholder(modifier = Modifier
                .fillMaxSize()
                .clip(shape))
        }
    )
}

@Composable
fun MoviePosterCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.CornerRadiusM),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        ) {
            PosterImage(
                posterPath = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Dimensions.PaddingM)
            ) {
                RatingPill(rating = movie.voteAverage)
                Spacer(modifier = Modifier.height(Dimensions.SpaceS))
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
