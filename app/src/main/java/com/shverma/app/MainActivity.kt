package com.shverma.app

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shverma.app.ui.journalentry.JournalEntryScreen
import com.shverma.app.ui.details.DetailScreen
import com.shverma.app.ui.home.HomeScreen
import com.shverma.app.ui.theme.AppTheme
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAIBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBarItemDefaults
import com.shverma.app.ui.theme.JournAIPink

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }

        setContent {
            val snackBarHostState =
                remember { SnackbarHostState() }

            AppTheme {
                AppNavigation(snackBarHostState)
            }
        }
    }
}


@Composable
fun AppNavigation(
    snackBarHostState: SnackbarHostState
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Routes.JournAIHomeRoute,
            label = "Home",
            icon = Icons.Filled.Home
        ),
        BottomNavItem(
            route = Routes.JournalRoute,
            label = "Journal",
            icon = Icons.Filled.Book
        ),
        BottomNavItem(
            route = Routes.InsightsRoute,
            label = "Insights",
            icon = Icons.Filled.BarChart
        ),
        BottomNavItem(
            route = Routes.ProfileRoute,
            label = "Profile",
            icon = Icons.Filled.Person
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == Routes.JournAIHomeRoute.toString()) {
                NavigationBar(
                    containerColor = JournAIBackground
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route.toString(),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JournAIPink,
                                unselectedIconColor = JournAIBrown,
                                selectedTextColor = JournAIPink,
                                unselectedTextColor = JournAIBrown
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.JournAIHomeRoute,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding(), top  = padding.calculateTopPadding())
        ) {
            composable<Routes.JournAIHomeRoute> {
                HomeScreen(
                    snackBarHostState = snackBarHostState,
                    onClickEntry = { itemId ->
                        navController.navigate(Routes.DetailRoute(itemId))
                    },
                    onStartWriting = {
                        navController.navigate(Routes.StartWritingRoute)
                    },
                )
            }
            composable<Routes.JournalRoute> {
                PlaceholderScreen("Journal")
            }
            composable<Routes.InsightsRoute> {
                PlaceholderScreen("Insights")
            }
            composable<Routes.ProfileRoute> {
                // TODO: Replace with actual ProfileScreen
                PlaceholderScreen("Profile")
            }
            composable<Routes.DetailRoute> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<Routes.DetailRoute>()
                DetailScreen(
                    itemId = args.id,
                    snackBarHostState = snackBarHostState,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<Routes.StartWritingRoute> {
                JournalEntryScreen(
                    modifier = Modifier
                )
            }
        }
    }
}

// Helper data class for bottom nav items
data class BottomNavItem(
    val route: Any,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// Placeholder composable for screens not yet implemented
@Composable
fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = "$label Screen")
    }
}

object Routes {

    @Serializable
    object JournAIHomeRoute

    @Serializable
    object JournalRoute

    @Serializable
    object InsightsRoute

    @Serializable
    object ProfileRoute

    @Serializable
    data class DetailRoute(val id: Int)

    @Serializable
    object StartWritingRoute
}