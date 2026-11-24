package com.simohypers.lucid

import com.simohypers.lucid.ui.navigation.destination.login.TermsDestination
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.collectAsState
import com.simohypers.lucid.expect.ui.rememberNotificationPermission
import com.simohypers.lucid.ui.navigation.destination.login.WelcomeDestination
import com.simohypers.lucid.ui.theme.rounded
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.materialkolor.ktx.themeColorOrNull
import com.simohypers.lucid.expect.ui.toImageBitmap
import com.simohypers.lucid.expect.ui.toReadableBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.simohypers.lucid.common.LibraryChipType
import com.simohypers.lucid.domain.data.player.GenericMediaItem
import com.simohypers.lucid.domain.manager.DataStoreManager
import com.simohypers.lucid.domain.manager.DataStoreManager.Values.TRUE
import com.simohypers.lucid.logger.Logger
import com.simohypers.lucid.expect.Orientation
import com.simohypers.lucid.expect.currentOrientation
import com.simohypers.lucid.ui.component.AppNavigationRail
import com.simohypers.lucid.ui.component.BusyPill
import com.simohypers.lucid.ui.component.UpdateBanner
import com.simohypers.lucid.ui.icon.ArrowForwardIos
import com.simohypers.lucid.ui.icon.LucidIcons
import com.simohypers.lucid.ui.navigation.destination.home.HomeDestination
import com.simohypers.lucid.ui.navigation.destination.home.NotificationDestination
import com.simohypers.lucid.ui.navigation.destination.library.LibraryDestination
import com.simohypers.lucid.ui.navigation.destination.search.SearchDestination
import com.simohypers.lucid.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import com.simohypers.lucid.ui.navigation.destination.library.MixForYouDestination
import com.simohypers.lucid.ui.navigation.destination.list.AlbumDestination
import com.simohypers.lucid.ui.navigation.destination.list.ArtistDestination
import com.simohypers.lucid.ui.navigation.destination.list.PlaylistDestination
import com.simohypers.lucid.ui.navigation.destination.player.FullscreenDestination
import com.simohypers.lucid.ui.navigation.graph.AppNavigationGraph
import com.simohypers.lucid.ui.component.LucidMiniPlayer
import com.simohypers.lucid.ui.component.LucidNavigationBar
import com.simohypers.lucid.ui.component.navigateToTab
import com.simohypers.lucid.ui.screen.player.NowPlayingScreen
import com.simohypers.lucid.ui.screen.player.NowPlayingScreenContent
import com.simohypers.lucid.ui.theme.AppTheme
import com.simohypers.lucid.ui.theme.desktopPanelDark
import com.simohypers.lucid.ui.theme.desktopWindowDark
import com.simohypers.lucid.ui.theme.desktopWindowLight
import com.simohypers.lucid.updater.Updater
import com.simohypers.lucid.utils.isAppLinkAvailable
import com.simohypers.lucid.utils.libraryTabOf
import com.simohypers.lucid.viewModel.SharedViewModel
import com.simohypers.lucid.viewModel.base.BaseViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import org.koin.compose.koinInject
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.notification
import lucid.composeapp.generated.resources.this_link_is_not_supported
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalFoundationApi::class)
@Composable
fun App(
    viewModel: SharedViewModel = koinInject(),
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navController = rememberNavController()
    // null until the saved session has been read, so a signed-in app never flashes the Welcome page.
    val dataStoreManager = koinInject<DataStoreManager>()
    val signedIn by remember { dataStoreManager.cookie.map { it.isNotEmpty() } }.collectAsState(initial = null)
    // Once signed in, ask for notifications (downloads, new releases): not on later launches of an app that was signed in all along.
    val notificationPermission = rememberNotificationPermission { }
    var cameFromWelcome by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(signedIn) {
        when (signedIn) {
            false -> cameFromWelcome = true
            true ->
                if (cameFromWelcome) {
                    cameFromWelcome = false
                    if (!notificationPermission.granted) notificationPermission.request()
                }
            null -> Unit
        }
    }
    val isDesktopShell = getPlatform() == Platform.Desktop

    val sleepTimerState by viewModel.sleepTimerState.collectAsStateWithLifecycle()
    val nowPlayingData by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val intent by viewModel.intent.collectAsStateWithLifecycle()

    // Mix for you comes from the signed-in YouTube account, so its tab follows the session — the
    // same condition that used to hide the chip inside Library.
    val isYouTubeLoggedIn by viewModel.getYouTubeLoggedIn().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val showMixForYouTab = isYouTubeLoggedIn == TRUE

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val appearance by viewModel.appearance.collectAsStateWithLifecycle()
    val artworkColors by viewModel.artworkColors.collectAsStateWithLifecycle()
    val flatProgressBar by viewModel.flatProgressBar.collectAsStateWithLifecycle()
    val artworkColor =
        rememberArtworkColor(
            url = nowPlayingData?.songEntity?.thumbnails ?: nowPlayingData?.mediaItem?.metadata?.artworkUri,
            enabled = artworkColors == TRUE,
            saved = viewModel.artworkColor,
            onColor = viewModel::setArtworkColor,
        )
    // MiniPlayer visibility: derived, never stored.
    //
    // This used to be a rememberSaveable Boolean written by a LaunchedEffect. Two things went
    // wrong with that. The effect only runs AFTER the first composition, so the first frame drew
    // whatever the initial value said — and rememberSaveable RESTORES a previously saved value,
    // so flipping that initial value from true to false changed nothing on a process that had
    // already saved true. The bar therefore showed, hid, and showed again on every start.
    //
    // Reading it straight from nowPlayingData removes both failure modes: there is no first-frame
    // guess to be wrong, and no saved copy to disagree with the source.
    val isShowMiniPlayer by remember {
        derivedStateOf {
            val item = nowPlayingData?.mediaItem
            item != null && item != GenericMediaItem.EMPTY
        }
    }

    // Now playing screen
    var isShowNowPlaylistScreen by rememberSaveable {
        mutableStateOf(false)
    }

    // Fullscreen
    var isInFullscreen by rememberSaveable {
        mutableStateOf(false)
    }

    var isNavBarVisible by rememberSaveable {
        mutableStateOf(true)
    }

    val hazeState =
        rememberHazeState()

    LaunchedEffect(intent) {
        val intent = intent ?: return@LaunchedEffect
        val data = intent.data
        Logger.d("MainActivity", "onCreate: $data")
        if (data != null) {
            if (data.scheme == "lucid" && data.host == "notification") {
                viewModel.setIntent(null)
                navController.navigate(
                    NotificationDestination,
                )
            } else if (data.scheme == "lucid" && data.host == "update") {
                // The update notification: show the banner, even with the banner switch off.
                viewModel.setIntent(null)
                runCatching { Updater.checkNow() }
            } else if (data.scheme == "lucid") {
                // lucid://watch?v=VIDEO_ID  (host="watch", no path)
                // lucid://playlist?list=PLAYLIST_ID
                // lucid://channel/CHANNEL_ID
                // The host IS the appPath.
                val appPath = data.host
                Logger.d("MainActivity", "App link, appPath: $appPath")
                // A path added to the when below must also go into APP_LINK_PATHS in AppLinks.kt.
                viewModel.setIntent(null)
                when (appPath) {
                    "watch" -> {
                        data.getQueryParameter("v")?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }

                    "playlist" -> {
                        data.getQueryParameter("list")?.let { playlistId ->
                            if (playlistId.startsWith("OLAK5uy_")) {
                                navController.navigate(AlbumDestination(browseId = playlistId))
                            } else if (playlistId.startsWith("VL")) {
                                navController.navigate(PlaylistDestination(playlistId = playlistId))
                            } else {
                                navController.navigate(PlaylistDestination(playlistId = "VL$playlistId"))
                            }
                        }
                    }

                    "channel", "c" -> {
                        // lucid://channel/UCxxx → segments = ["UCxxx"]
                        data.pathSegments.firstOrNull()?.let {
                            if (it.startsWith("UC")) {
                                navController.navigate(ArtistDestination(channelId = it))
                            } else {
                                viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                            }
                        }
                    }

                    "album" -> {
                        data.getQueryParameter("id")?.let { albumId ->
                            navController.navigate(AlbumDestination(browseId = albumId))
                        }
                    }

                    // lucid://search → the search screen (the launcher shortcut)
                    "search" -> {
                        navController.navigateToTab(SearchDestination)
                    }

                    // lucid://library                     → the Library tab
                    // lucid://library?type=favorite       → one of its collections
                    // Added for the Playlists widget, whose shortcuts have to reach these
                    // screens from the home screen without the app already running.
                    "library" -> {
                        val type = data.getQueryParameter("type")
                        if (type.isNullOrBlank()) {
                            // lucid://library?tab=chart → the Library tab on one of its chips,
                            // named as LibraryChipType stores them.
                            val tab = libraryTabOf(data)
                            if (!isAppLinkAvailable(appPath)) {
                                viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                            } else {
                                viewModel.requestLibraryTab(tab)
                                navController.navigateToTab(LibraryDestination)
                            }
                        } else {
                            navController.navigate(LibraryDynamicPlaylistDestination(type = type))
                        }
                    }

                    else -> {
                        viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                    }
                }
            } else {
                Logger.d("MainActivity", "onCreate: $data")
                when (val path = data.pathSegments.firstOrNull()) {
                    "playlist" -> {
                        data
                            .getQueryParameter("list")
                            ?.let { playlistId ->
                                viewModel.setIntent(null)
                                if (playlistId.startsWith("OLAK5uy_")) {
                                    navController.navigate(
                                        AlbumDestination(
                                            browseId = playlistId,
                                        ),
                                    )
                                } else if (playlistId.startsWith("VL")) {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = playlistId,
                                        ),
                                    )
                                } else {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = "VL$playlistId",
                                        ),
                                    )
                                }
                            }
                    }

                    "channel", "c" -> {
                        data.lastPathSegment?.let { artistId ->
                            if (artistId.startsWith("UC")) {
                                viewModel.setIntent(null)
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = artistId,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(
                                    getString(
                                        Res.string.this_link_is_not_supported,
                                    ),
                                )
                            }
                        }
                    }

                    else -> {
                        when {
                            path == "watch" -> data.getQueryParameter("v")
                            data.host == "youtu.be" -> path
                            else -> null
                        }?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }
                }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val onWelcome =
        navBackStackEntry?.destination?.let { it.hasRoute(WelcomeDestination::class) || it.hasRoute(TermsDestination::class) } == true
    LaunchedEffect(navBackStackEntry) {
        Logger.d("MainActivity", "Current destination: ${navBackStackEntry?.destination?.route}")
        if (navBackStackEntry?.destination?.route?.contains("FullscreenDestination") == true) {
            isShowNowPlaylistScreen = false
        }
        isInFullscreen = navBackStackEntry?.destination?.hierarchy?.any {
            it.hasRoute(FullscreenDestination::class)
        } == true
    }
    // Off by default on the phone; desktop always opens it.
    LaunchedEffect(Unit) {
        BaseViewModel.userPlays.collect {
            if (viewModel.getOpenPlayerOnPlay().first()) isShowNowPlaylistScreen = true
        }
    }
    LaunchedEffect(showMixForYouTab) {
        // Same for signing out of YouTube: the Mix for you tab goes away, so nobody may be left
        // standing on a screen that has no mixes to show and no tab pointing at it.
        if (!showMixForYouTab &&
            navBackStackEntry?.destination?.hierarchy?.any {
                it.hasRoute(MixForYouDestination::class)
            } == true
        ) {
            navController.navigateToTab(HomeDestination)
        }
    }
    var isScrolledToTop by rememberSaveable {
        mutableStateOf(false)
    }
    val isTablet = windowSize.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isTabletLandscape = isTablet && currentOrientation() == Orientation.LANDSCAPE

    AppTheme(
        themeMode = themeMode,
        appearance = appearance,
        artworkColor = artworkColor,
        flatProgressBar = flatProgressBar,
    ) {
        // Read inside AppTheme so MaterialTheme reflects the resolved scheme (light background is #FFFFFF).
        val isLightScheme = MaterialTheme.colorScheme.background.luminance() > 0.5f

        // The desktop shell is a window colour with panels floating on it. The two schemes mirror
        // each other: the window takes the extreme (pure black / pure white) and the panel steps
        // one shade back towards the middle, so the panels read as raised either way.
        val desktopWindow = if (isLightScheme) desktopWindowLight else desktopWindowDark
        val desktopPanel =
            if (isLightScheme) MaterialTheme.colorScheme.surfaceContainer else desktopPanelDark
        Scaffold(
            containerColor =
                if (isDesktopShell) desktopWindow else MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!isTablet) {
                    AnimatedVisibility(
                        isNavBarVisible && !onWelcome,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut(),
                    ) {
                        // The mini player sits right above the tabs; both disappear together when a
                        // screen hides the bar (the fullscreen video player, for one).
                        Column {
                            AnimatedVisibility(
                                isShowMiniPlayer,
                                enter = fadeIn() + slideInHorizontally(),
                                exit = fadeOut(),
                            ) {
                                LucidMiniPlayer(
                                    onOpen = { isShowNowPlaylistScreen = true },
                                    modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 6.dp),
                                )
                            }
                            LucidNavigationBar(navController = navController) { klass ->
                                viewModel.reloadDestination(klass)
                            }
                        }
                    }
                }
            },
            content = { innerPadding ->
                Box(Modifier.fillMaxSize()) {
                    Row(
                        Modifier.fillMaxSize(),
                    ) {
                        if (isTablet && !isInFullscreen && !onWelcome) {
                            AppNavigationRail(
                                navController = navController,
                                showMixForYouTab = showMixForYouTab,
                            ) { klass ->
                                viewModel.reloadDestination(klass)
                            }
                        }
                        // Desktop only: the content sits in its own rounded panel floating on a
                        // pure black window, Spotify style, while the rail stays flat black
                        // outside it. Phones keep one continuous surface — the inset only reads
                        // as deliberate when there is a window frame around it.
                        Box(
                            Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .then(
                                    if (isDesktopShell) {
                                        Modifier
                                            .padding(8.dp)
                                            .clip(rounded(12.dp))
                                            .background(desktopPanel)
                                    } else {
                                        Modifier
                                    },
                                ),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .hazeSource(hazeState),
                            ) {
                                // Waits for the saved session to be read: the start page is Home when signed in,
                                // the Welcome page when not, and it is the first thing the graph is built with.
                                signedIn?.let { isSignedIn ->
                                    AppNavigationGraph(
                                        innerPadding = innerPadding,
                                        navController = navController,
                                        startDestination = if (isSignedIn) HomeDestination else WelcomeDestination,
                                        hideNavBar = {
                                            isNavBarVisible = false
                                        },
                                        showNavBar = {
                                            isNavBarVisible = true
                                        },
                                        showNowPlayingSheet = {
                                            isShowNowPlaylistScreen = true
                                        },
                                        onScrolling = {
                                            isScrolledToTop = it
                                        },
                                    )
                                }
                            }
                            // The phone's mini player, floating over the content. Hidden while the Now
                            // Playing panel beside it is open: the panel has the same controls.
                            this@Row.AnimatedVisibility(
                                modifier =
                                    Modifier
                                        .padding(innerPadding)
                                        .align(Alignment.BottomCenter),
                                visible =
                                    isShowMiniPlayer && isTablet && !isInFullscreen &&
                                        !(isTabletLandscape && isShowNowPlaylistScreen),
                                enter = fadeIn() + slideInHorizontally(),
                                exit = fadeOut(),
                            ) {
                                LucidMiniPlayer(
                                    onOpen = { isShowNowPlaylistScreen = true },
                                    modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 6.dp),
                                )
                            }
                        }
                        if (isTablet && isTabletLandscape && !isInFullscreen) {
                            AnimatedVisibility(
                                isShowNowPlaylistScreen,
                                enter = expandHorizontally() + fadeIn(),
                                exit = fadeOut() + shrinkHorizontally(),
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxHeight()
                                        // A camera cutout or nav bar on this side comes on top of
                                        // the width below, not out of it.
                                        .padding(end = innerPadding.calculateEndPadding(LocalLayoutDirection.current))
                                        // Never narrower than a phone held upright, which every
                                        // player style is laid out for: 35 % of a phone on its
                                        // side is ~260dp and cut the rows of buttons off (#13).
                                        .widthIn(min = PANEL_MIN_WIDTH + 8.dp)
                                        .fillMaxWidth(0.35f),
                                ) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        Modifier
                                            .then(
                                                // Matches the inset of the content panel so the two
                                                // read as a pair of floating cards, not one panel
                                                // with a seam down the middle.
                                                if (isDesktopShell) {
                                                    Modifier.padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                                                } else {
                                                    Modifier
                                                },
                                            ).clip(
                                                rounded(12.dp),
                                            ).then(
                                                if (isDesktopShell) {
                                                    Modifier.background(desktopPanel)
                                                } else {
                                                    Modifier
                                                },
                                            ),
                                    ) {
                                        NowPlayingScreenContent(
                                            navController = navController,
                                            sharedViewModel = viewModel,
                                            isExpanded = true,
                                            dismissIcon = LucidIcons.ArrowForwardIos,
                                        ) {
                                            isShowNowPlaylistScreen = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                    BusyPill(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = innerPadding.calculateBottomPadding() + 12.dp),
                    )
                    UpdateBanner(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 8.dp)
                            .padding(bottom = innerPadding.calculateBottomPadding() + 8.dp),
                    )
                }

                if (isShowNowPlaylistScreen && !isTabletLandscape) {
                    NowPlayingScreen(
                        navController = navController,
                    ) {
                        isShowNowPlaylistScreen = false
                    }
                }

                // Desktop's quiet update check: on open, then every six hours while the app stays up.
                LaunchedEffect(Unit) {
                    while (true) {
                        Updater.checkQuiet()
                        delay(Updater.INTERVAL)
                    }
                }

                // The fade and the pause are the whole ending; this only resets the timer to off.
                LaunchedEffect(sleepTimerState.isDone) {
                    if (sleepTimerState.isDone) viewModel.stopSleepTimer()
                }
            },
        )
    }
}

/**
 * The colour of the cover at [url], for "Adapt colors to artwork"; null when [url] is null. Picked
 * the way the M3 Expressive player seeds its scheme. The previous colour stays until the new cover
 * is decoded; a colourless cover, or one that fails to load, then drops back to the theme, as on
 * desktop (#522: keeping the last track's colour paints this one in a cover it doesn't have).
 * The mini player has already loaded the same cover, so this is a disk-cache read.
 */
@Composable
private fun rememberArtworkColor(
    url: String?,
    enabled: Boolean,
    // The last colour, kept by the view model: a cold start opens in it while the saved queue comes
    // back, and a recreated activity carries on in it instead of flashing the theme's accent.
    saved: Color?,
    onColor: (Color?) -> Unit,
): Color? {
    val context = LocalPlatformContext.current
    var color by remember { mutableStateOf(saved) }
    // Until the first track shows up, no track means the queue isn't restored yet, not nothing playing.
    // ponytail: a saved queue that fails to come back leaves the saved colour up until something plays.
    var sawTrack by remember { mutableStateOf(false) }
    LaunchedEffect(url, enabled) {
        if (!enabled || url == null) {
            if (!enabled || sawTrack) color = null
        } else {
            sawTrack = true
            val image =
                SingletonImageLoader
                    .get(context)
                    .execute(ImageRequest.Builder(context).data(url).size(ARTWORK_SEED_PX).build())
                    .image
            // ponytail: decoded on the track change, not one track early as desktop does; prewarm the
            // next cover if the colour visibly lands after the artwork.
            color =
                image?.let {
                    withContext(Dispatchers.Default) {
                        runCatching { it.toImageBitmap().toReadableBitmap(ARTWORK_SEED_PX, ARTWORK_SEED_PX).themeColorOrNull() }.getOrNull()
                    }
                }
        }
        onColor(color)
    }
    return color.takeIf { enabled }
}

private const val ARTWORK_SEED_PX = 112

// The tester's phone held upright (#13); its Classic player was checked at this width.
private val PANEL_MIN_WIDTH = 384.dp
