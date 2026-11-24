package com.simohypers.lucid.ui.navigation.graph

import com.simohypers.lucid.ui.screen.login.TermsScreen
import com.simohypers.lucid.ui.navigation.destination.login.TermsDestination
import com.simohypers.lucid.ui.screen.login.WelcomeScreen
import com.simohypers.lucid.ui.navigation.destination.login.LoginDestination
import com.simohypers.lucid.ui.navigation.destination.login.WelcomeDestination
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.simohypers.lucid.ui.component.lucidTabIndex
import com.simohypers.lucid.ui.navigation.destination.home.HomeDestination
import com.simohypers.lucid.ui.theme.ForceDarkContent
import com.simohypers.lucid.ui.navigation.destination.library.LibraryDestination
import com.simohypers.lucid.ui.navigation.destination.library.MixForYouDestination
import com.simohypers.lucid.ui.navigation.destination.player.FullscreenDestination
import com.simohypers.lucid.ui.navigation.destination.search.SearchDestination
import com.simohypers.lucid.ui.screen.home.HomeScreen
import com.simohypers.lucid.ui.screen.library.LibraryScreen
import com.simohypers.lucid.ui.screen.library.MixForYouScreen
import com.simohypers.lucid.ui.screen.other.SearchScreen
import com.simohypers.lucid.ui.screen.player.FullscreenPlayer

@Composable
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
fun AppNavigationGraph(
    innerPadding: PaddingValues,
    navController: NavHostController,
    startDestination: Any = HomeDestination,
    hideNavBar: () -> Unit = { },
    showNavBar: (shouldShowNowPlayingSheet: Boolean) -> Unit = { },
    showNowPlayingSheet: () -> Unit = {},
    onScrolling: (onTop: Boolean) -> Unit = {},
) {
    NavHost(
        navController,
        startDestination = startDestination,
        // Opening a page slides it in from the right and back slides it out the same way. Switching
        // tabs slides the way the tab bar reads; leaving a page for another tab only fades, since the
        // page has no place in the bar.
        enterTransition = {
            when (val dir = tabDirection(initialState.destination, targetState.destination)) {
                0 -> fadeIn()
                else -> fadeIn() + slideInHorizontally { it * dir }
            }
        },
        exitTransition = {
            when (val dir = tabDirection(initialState.destination, targetState.destination)) {
                0 -> fadeOut()
                else -> fadeOut() + slideOutHorizontally { -it * dir }
            }
        },
        popEnterTransition = {
            fadeIn() + slideInHorizontally { -it }
        },
        popExitTransition = {
            fadeOut() + slideOutHorizontally { it }
        },
    ) {
        // Bottom bar destinations
        composable<HomeDestination> {
            HomeScreen(
                onScrolling = onScrolling,
                navController = navController,
            )
        }
        composable<SearchDestination> {
            SearchScreen(
                navController = navController,
                innerPadding = innerPadding,
            )
        }
        composable<LibraryDestination> {
            LibraryScreen(
                innerPadding = innerPadding,
                navController = navController,
                onScrolling = onScrolling,
            )
        }
        // Only reachable as a tab while signed in to YouTube
        composable<MixForYouDestination> {
            MixForYouScreen(
                innerPadding = innerPadding,
                navController = navController,
                onScrolling = onScrolling,
            )
        }
        composable<FullscreenDestination> {
            ForceDarkContent {
                FullscreenPlayer(
                    navController,
                    hideNavBar = hideNavBar,
                    showNavBar = {
                        showNavBar.invoke(true)
                        showNowPlayingSheet.invoke()
                    },
                )
            }
        }
        // Home screen graph
        homeScreenGraph(
            innerPadding = innerPadding,
            navController = navController,
        )
        // Library screen graph
        libraryScreenGraph(
            innerPadding = innerPadding,
            navController = navController,
        )
        // List screen graph
        listScreenGraph(
            innerPadding = innerPadding,
            navController = navController,
        )
        // The signed-out first page: Continue with Google opens the sign-in page below
        composable<WelcomeDestination> {
            WelcomeScreen(
                onContinueWithGoogle = { navController.navigate(LoginDestination()) },
                onOpenTerms = { navController.navigate(TermsDestination) },
            )
        }
        composable<TermsDestination> {
            TermsScreen(onBack = { navController.navigateUp() })
        }
        // Login screen graph
        loginScreenGraph(
            innerPadding = innerPadding,
            navController = navController,
            hideBottomBar = hideNavBar,
            showBottomBar = {
                showNavBar(false)
            },
        )
    }
}

/** 1 slides forward (from the right), -1 backward, 0 fades: a tab switch that starts on a page. */
private fun tabDirection(
    from: NavDestination,
    to: NavDestination,
): Int {
    val fromTab = lucidTabIndex(from)
    val toTab = lucidTabIndex(to)
    return when {
        toTab < 0 -> 1
        fromTab < 0 -> 0
        toTab < fromTab -> -1
        else -> 1
    }
}
