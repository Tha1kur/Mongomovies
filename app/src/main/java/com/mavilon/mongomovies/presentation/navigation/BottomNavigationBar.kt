package com.mavilon.mongomovies.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mavilon.mongomovies.R
import com.mavilon.mongomovies.ui.theme.AccentCyan
import com.mavilon.mongomovies.ui.theme.TextSecondary

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Home", R.drawable.home),
    BottomNavItem(Screen.Search.route, "Search", R.drawable.search),
    BottomNavItem(Screen.Watchlist.route, "Watch list", R.drawable.save)
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentCyan,
                    selectedTextColor = AccentCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
