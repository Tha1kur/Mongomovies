package com.mavilon.mongomovies.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.mavilon.mongomovies.R

/**
 * Centralized icon provider for MongoMovies app.
 * All icons are SVG vector drawables for consistent visual language.
 */
object AppIcons {
    
    // Navigation Icons
    val Home: Int @Composable get() = R.drawable.home
    val Search: Int @Composable get() = R.drawable.search
    val Watchlist: Int @Composable get() = R.drawable.ticket
    
    // Action Icons
    val Back: Int @Composable get() = R.drawable.arrow_left
    val Bookmark: Int @Composable get() = R.drawable.save
    val BookmarkFilled: Int @Composable get() = R.drawable.save // Same icon, will be filled via tint
    
    // Content Icons
    val Star: Int @Composable get() = R.drawable.star
    val Calendar: Int @Composable get() = R.drawable.calendarblank
    val Clock: Int @Composable get() = R.drawable.clock
    
    // Search variant
    val SearchAlt: Int @Composable get() = R.drawable.search_2
}

/**
 * Helper to load ImageVector from drawable resource
 */
@Composable
fun iconVector(@DrawableRes id: Int): ImageVector {
    return ImageVector.vectorResource(id = id)
}
