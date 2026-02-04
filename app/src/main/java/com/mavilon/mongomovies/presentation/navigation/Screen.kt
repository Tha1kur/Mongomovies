package com.mavilon.mongomovies.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Watchlist : Screen("watchlist")
    object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }
}
