package com.mavilon.mongomovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mavilon.mongomovies.presentation.navigation.BottomNavigationBar
import com.mavilon.mongomovies.presentation.navigation.NavGraph
import com.mavilon.mongomovies.presentation.navigation.Screen
import com.mavilon.mongomovies.ui.theme.BackgroundPrimary
import com.mavilon.mongomovies.ui.theme.MongomoviesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val container = (application as MongoMoviesApp).container
        
        setContent {
            MongomoviesTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(
                    color = BackgroundPrimary,
                    darkIcons = false
                )
                
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                // Determine if bottom bar should be shown
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Search.route,
                    Screen.Watchlist.route
                )
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        // Pop up to the start destination
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        container = container,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}