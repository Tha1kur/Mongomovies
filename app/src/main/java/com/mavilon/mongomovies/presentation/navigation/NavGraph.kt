package com.mavilon.mongomovies.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mavilon.mongomovies.di.AppContainer
import com.mavilon.mongomovies.presentation.details.MovieDetailsScreen
import com.mavilon.mongomovies.presentation.home.HomeScreen
import com.mavilon.mongomovies.presentation.search.SearchScreen
import com.mavilon.mongomovies.presentation.watchlist.WatchlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                container = container,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                container = container,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }
        
        composable(Screen.Watchlist.route) {
            WatchlistScreen(
                container = container,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                }
            )
        }
        
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            MovieDetailsScreen(
                movieId = movieId,
                container = container,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
