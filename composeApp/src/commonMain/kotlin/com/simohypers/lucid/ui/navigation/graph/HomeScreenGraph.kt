package com.simohypers.lucid.ui.navigation.graph

import com.simohypers.lucid.ui.screen.home.DevicesSettingsScreen
import com.simohypers.lucid.ui.navigation.destination.home.DevicesSettingsDestination
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.simohypers.lucid.ui.navigation.destination.home.AppLogDestination
import com.simohypers.lucid.ui.navigation.destination.home.CreditDestination
import com.simohypers.lucid.ui.navigation.destination.home.WhatsNewDestination
import com.simohypers.lucid.ui.navigation.destination.home.ListenTogetherDestination
import com.simohypers.lucid.ui.navigation.destination.home.MoodDestination
import com.simohypers.lucid.ui.navigation.destination.home.NotificationDestination
import com.simohypers.lucid.ui.navigation.destination.home.RecentlySongsDestination
import com.simohypers.lucid.ui.navigation.destination.home.SettingsDestination
import com.simohypers.lucid.ui.navigation.destination.home.LyricsSourcesDestination
import com.simohypers.lucid.ui.navigation.destination.home.SettingsSectionDestination
import com.simohypers.lucid.ui.screen.home.ListenTogetherScreen
import com.simohypers.lucid.ui.screen.home.MoodScreen
import com.simohypers.lucid.ui.screen.home.NotificationScreen
import com.simohypers.lucid.ui.screen.home.RecentlySongsScreen
import com.simohypers.lucid.ui.screen.home.SettingScreen
import com.simohypers.lucid.ui.screen.home.LyricsSourcesScreen
import com.simohypers.lucid.ui.screen.home.SettingsSection
import com.simohypers.lucid.ui.screen.other.AppLogScreen
import com.simohypers.lucid.ui.screen.other.CreditScreen
import com.simohypers.lucid.ui.screen.other.WhatsNewScreen

fun NavGraphBuilder.homeScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<CreditDestination> {
        CreditScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<AppLogDestination> {
        AppLogScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<WhatsNewDestination> {
        WhatsNewScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<MoodDestination> { entry ->
        val route = entry.toRoute<MoodDestination>()
        MoodScreen(
            navController = navController,
            params = route.params,
            color = route.color,
            artwork = route.artwork,
            kicker = route.kicker,
        )
    }
    composable<ListenTogetherDestination> {
        ListenTogetherScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<NotificationDestination> {
        NotificationScreen(
            navController = navController,
        )
    }
    composable<RecentlySongsDestination> {
        RecentlySongsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<SettingsDestination> {
        SettingScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<LyricsSourcesDestination> {
        LyricsSourcesScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<DevicesSettingsDestination> {
        DevicesSettingsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<SettingsSectionDestination> { entry ->
        SettingScreen(
            navController = navController,
            innerPadding = innerPadding,
            section = SettingsSection.valueOf(entry.toRoute<SettingsSectionDestination>().section),
        )
    }
}