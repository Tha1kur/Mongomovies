package com.mavilon.mongomovies.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Watchlist : Screen("watchlist")

    object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }

    object TrailerPlayer : Screen("trailer/{youtubeKey}") {
        fun createRoute(youtubeKey: String) = "trailer/${Uri.encode(youtubeKey)}"
    }
}

