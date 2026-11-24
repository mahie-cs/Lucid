package com.simohypers.lucid.ui.component

import com.simohypers.lucid.ui.navigation.destination.home.SettingsDestination
import com.simohypers.lucid.ui.icon.Settings
import lucid.composeapp.generated.resources.settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.simohypers.lucid.ui.icon.Home
import com.simohypers.lucid.ui.icon.LibraryMusic
import com.simohypers.lucid.ui.icon.Search
import com.simohypers.lucid.ui.icon.Sensors
import com.simohypers.lucid.ui.icon.LucidIcons
import com.simohypers.lucid.ui.navigation.destination.home.HomeDestination
import com.simohypers.lucid.ui.navigation.destination.library.LibraryDestination
import com.simohypers.lucid.ui.navigation.destination.library.MixForYouDestination
import com.simohypers.lucid.ui.navigation.destination.search.SearchDestination
import org.jetbrains.compose.resources.StringResource
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.home
import lucid.composeapp.generated.resources.library
import lucid.composeapp.generated.resources.mix
import lucid.composeapp.generated.resources.search

sealed class BottomNavScreen(
    val ordinal: Int,
    val destination: Any,
    val title: StringResource,
    val icon: @Composable () -> Unit,
) {
    data object Home : BottomNavScreen(
        ordinal = 0,
        destination = HomeDestination,
        title = Res.string.home,
        icon = {
            Icon(
                LucidIcons.Home,
                contentDescription = null,
            )
        },
    )

    data object Search : BottomNavScreen(
        ordinal = 1,
        destination = SearchDestination,
        title = Res.string.search,
        icon = {
            Icon(
                LucidIcons.Search,
                contentDescription = null,
            )
        },
    )

    data object Library : BottomNavScreen(
        ordinal = 2,
        destination = LibraryDestination,
        title = Res.string.library,
        icon = {
            Icon(
                imageVector = LucidIcons.LibraryMusic,
                contentDescription = null,
            )
        },
    )

    data object Settings : BottomNavScreen(
        ordinal = 3,
        destination = SettingsDestination,
        title = Res.string.settings,
        icon = {
            Icon(
                imageVector = LucidIcons.Settings,
                contentDescription = null,
            )
        },
    )

    // Only shown while signed in to YouTube — an anonymous session gets no mixes.
    // Labelled "Mix", not "Mix for you": the full title is the widest label in the bar and forces
    // every tab to be that wide. The screen itself still uses the full title.
    data object MixForYou : BottomNavScreen(
        ordinal = 4,
        destination = MixForYouDestination,
        title = Res.string.mix,
        icon = {
            Icon(
                imageVector = LucidIcons.Sensors,
                contentDescription = null,
            )
        },
    )
}