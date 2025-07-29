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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shverma.app.data.preference.DataStoreHelper
import com.shverma.app.ui.details.DetailScreen
import com.shverma.app.ui.home.HomeScreen
import com.shverma.app.ui.journal.JournalsListScreen
import com.shverma.app.ui.journalentry.JournalEntryScreen
import com.shverma.app.ui.login.LoginScreen
import com.shverma.app.ui.theme.AppTheme
import com.shverma.app.ui.theme.JournAIBackground
import com.shverma.app.ui.theme.JournAIBrown
import com.shverma.app.ui.theme.JournAIPink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

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
                AppNavigation(snackBarHostState, dataStoreHelper)
            }
        }
    }
}


@Composable
fun AppNavigation(
    snackBarHostState: SnackbarHostState,
    dataStoreHelper: DataStoreHelper
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<Any?>(null) }
    var checkedToken by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val token = dataStoreHelper.accessToken.first()
        startDestination =
            if (!token.isNullOrBlank()) Routes.JournAIHomeRoute else Routes.LoginRoute
        checkedToken = true
    }

    if (!checkedToken || startDestination == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
    }

    val bottomNavItems = navItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val mainRoutes = setOf(
                Routes.JournAIHomeRoute::class.qualifiedName,
                Routes.JournalRoute::class.qualifiedName,
                Routes.InsightsRoute::class.qualifiedName,
                Routes.ProfileRoute::class.qualifiedName
            )
            if (currentRoute in mainRoutes) {
                NavigationBar(
                    containerColor = JournAIBackground
                ) {
                    bottomNavItems.forEach { item ->
                        val itemRoute = when (item.route) {
                            is Routes.JournAIHomeRoute -> Routes.JournAIHomeRoute::class.qualifiedName
                            is Routes.JournalRoute -> Routes.JournalRoute::class.qualifiedName
                            is Routes.InsightsRoute -> Routes.InsightsRoute::class.qualifiedName
                            is Routes.ProfileRoute -> Routes.ProfileRoute::class.qualifiedName
                            else -> item.route.toString()
                        }
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == itemRoute,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = JournAIBrown,
                                unselectedIconColor = JournAIBrown,
                                selectedTextColor = JournAIBrown,
                                indicatorColor = JournAIPink,
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
            startDestination = startDestination!!,
            modifier = Modifier.padding(
                bottom = padding.calculateBottomPadding(),
                top = padding.calculateTopPadding()
            )
        ) {
            composable<Routes.LoginRoute> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.JournAIHomeRoute) {
                            popUpTo(Routes.LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<Routes.JournAIHomeRoute> {
                HomeScreen(
                    snackBarHostState = snackBarHostState,
                    onClickEntry = { date ->
                        navController.navigate(Routes.DetailRoute(date))
                    },
                    onStartWriting = {
                        navController.navigate(Routes.StartWritingRoute)
                    },
                )
            }
            composable<Routes.JournalRoute> {
                JournalsListScreen(
                    snackBarHostState = snackBarHostState, onGetAiTips = {
                        navController.popBackStack()
                    }
                )
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
                    date = args.date,
                    snackBarHostState = snackBarHostState,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable<Routes.StartWritingRoute> {
                JournalEntryScreen(
                    snackBarHostState = snackBarHostState,
                    modifier = Modifier, onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun navItems(): List<BottomNavItem> = listOf(
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
    data class DetailRoute(val date: String)

    @Serializable
    object StartWritingRoute

    @Serializable
    object LoginRoute
}