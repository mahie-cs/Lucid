package com.simohypers.lucid.ui.screen.player.content

import com.simohypers.lucid.ui.screen.player.content.applemusic.CoverLyricLine
import com.simohypers.lucid.ui.theme.rounded
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import com.simohypers.lucid.expect.ui.PlatformBackHandler
import com.simohypers.lucid.ui.component.QueueActions
import com.simohypers.lucid.ui.component.QueueList
import com.simohypers.lucid.ui.component.QueueTopBar
import com.simohypers.lucid.ui.component.SongHeader
import com.simohypers.lucid.ui.component.lyrics.ShareLyricsSheet
import com.simohypers.lucid.ui.component.lyrics.toShareLyricsLines
import com.simohypers.lucid.ui.icon.Share
import com.simohypers.lucid.viewModel.SharedViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lucid.composeapp.generated.resources.share_lyrics
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.simohypers.lucid.common.Config.MAIN_PLAYER
import com.simohypers.lucid.domain.data.player.AudioOutputKind
import com.simohypers.lucid.domain.mediaservice.handler.MediaPlayerHandler
import com.simohypers.lucid.domain.mediaservice.handler.RepeatState
import com.simohypers.lucid.expect.ui.MediaPlayerViewWithSubtitle
import com.simohypers.lucid.expect.ui.isLyricsBlurSupported
import com.simohypers.lucid.expect.ui.toImageBitmap
import com.simohypers.lucid.extension.elapsedLabel
import com.simohypers.lucid.extension.lengthLabel
import com.simohypers.lucid.ui.component.rememberHolderPainter
import com.simohypers.lucid.ui.icon.Favorite
import com.simohypers.lucid.ui.icon.FavoriteBorder
import com.simohypers.lucid.ui.icon.Fullscreen
import com.simohypers.lucid.ui.component.PlayingOnLabel
import com.simohypers.lucid.ui.component.rememberRemoteDevice
import com.simohypers.lucid.ui.icon.Headphones
import com.simohypers.lucid.ui.icon.Lyrics
import com.simohypers.lucid.ui.icon.MoreVert
import com.simohypers.lucid.ui.icon.Pause
import com.simohypers.lucid.ui.icon.PlayArrow
import com.simohypers.lucid.ui.icon.PlaylistAdd
import com.simohypers.lucid.ui.icon.QueueMusic
import com.simohypers.lucid.ui.icon.Repeat
import com.simohypers.lucid.ui.icon.RepeatOne
import com.simohypers.lucid.ui.icon.Shuffle
import com.simohypers.lucid.ui.component.doubleTapToLike
import com.simohypers.lucid.ui.icon.LucidIcons
import com.simohypers.lucid.ui.icon.Check
import com.simohypers.lucid.ui.icon.Download
import com.simohypers.lucid.ui.icon.SkipNext
import com.simohypers.lucid.ui.icon.SkipPrevious
import com.simohypers.lucid.ui.screen.player.content.applemusic.AudioOutputSheetHost
import com.simohypers.lucid.ui.screen.player.content.expressive.WavySeekBar
import com.simohypers.lucid.ui.theme.typo
import com.simohypers.lucid.viewModel.UIEvent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.downloaded
import lucid.composeapp.generated.resources.download
import com.simohypers.lucid.domain.data.entities.DownloadState
import androidx.compose.material3.CircularProgressIndicator
import lucid.composeapp.generated.resources.lyrics
import lucid.composeapp.generated.resources.now_playing
import lucid.composeapp.generated.resources.waiting_for_network
import lucid.composeapp.generated.resources.share

private val ArtworkShape get() = rounded(16.dp)
private val SmallShape get() = rounded(12.dp)
private val FROST_BLUR = 80.dp
private const val FROST_DECODE_PX = 256

// How far past the touch slop a drag up the cover goes before it opens the queue.
private val SWIPE_UP = 40.dp

// How far the cover, title, seek bar and pills sit above where the leftover height would put them.
private val RAISE = 16.dp

// The page's side margin, and the least room above and below the cover (its shadow draws there).
private val GUTTER = 24.dp
private val COVER_GAP = 12.dp

/**
 * The Classic full player, the default: a large cover with the current lyric line under it, title
 * and artist with like, the wavy seek bar, a row of rounded transport buttons, and a
 * row of squares at the bottom (Queue, Lyrics and the rest), the audio output top right. Queue and Lyrics are views of the same
 * page, like the Apple Music style's: they take the cover's place and the controls stay put.
 * Colours are the app's own (the cover's with Adapt colors to artwork on), over the cover itself,
 * frosted.
 */
@Composable
fun NowPlayingContentLucid(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    ClassicLayout(state, actions)
}

/**
 * Classic's Lyrics view as a page of its own: what M3 Expressive opens for fullscreen lyrics
 * (Apple Music has its own lyrics view). The seek bar and pills come with it, the row of squares doesn't; [onClose] closes it.
 */
@Composable
internal fun ClassicLyricsPage(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    onClose: () -> Unit,
) {
    ClassicLayout(state, actions, lyricsPageClose = onClose)
}

/** What the middle of the Classic page shows. The controls under it are the same for all three. */
private enum class ClassicView { PLAYER, QUEUE, LYRICS }

/** The Classic page; with [lyricsPageClose] it is [ClassicLyricsPage], held on Lyrics. */
@Composable
private fun ClassicLayout(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    lyricsPageClose: (() -> Unit)? = null,
) {
    val sharedViewModel = koinInject<SharedViewModel>()
    var view by rememberSaveable { mutableStateOf(if (lyricsPageClose != null) ClassicView.LYRICS else ClassicView.PLAYER) }
    // Back leaves Queue or Lyrics for the cover before it closes the player.
    PlatformBackHandler(enabled = view != ClassicView.PLAYER) { lyricsPageClose?.invoke() ?: run { view = ClassicView.PLAYER } }
    // The mini player's lyrics button opens the player on its lyrics.
    val lyricsRequested by sharedViewModel.fullscreenLyricsRequest.collectAsStateWithLifecycle()
    LaunchedEffect(lyricsRequested) {
        if (lyricsRequested) {
            view = ClassicView.LYRICS
            sharedViewModel.consumeFullscreenLyricsRequest()
        }
    }
    // On Queue and Lyrics the pills tuck away after a few seconds without a touch, to give the list
    // and the lines room; a tap or a list scrolling brings them back and starts the wait again. A
    // drag that moves the sheet doesn't: the pills would grow under the finger while the page slides.
    var pillsShown by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var hideJob by remember { mutableStateOf<Job?>(null) }
    val touched = {
        pillsShown = true
        hideJob?.cancel()
        hideJob =
            scope.launch {
                delay(4000)
                if (view != ClassicView.PLAYER) pillsShown = false
            }
    }
    LaunchedEffect(view) { touched() }
    // Tapping the square of the view on screen goes back to the cover.
    val toggle: (ClassicView) -> Unit = { to -> view = if (view == to) ClassicView.PLAYER else to }

    val background = MaterialTheme.colorScheme.background
    Box(Modifier.fillMaxSize().background(background)) {
        // Modifier.blur does nothing before Android 12; the cover-coloured gradient stands in there.
        if (isLyricsBlurSupported()) {
            FrostedCover(state.screenData.thumbnailURL, wash = background)
        } else {
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to state.startColor.value.copy(alpha = 0.55f), 0.75f to background)))
        }
        Column(
            Modifier
                .fillMaxSize()
                // Seen after the children: a gesture none of whose moves was taken is a tap (a lyric
                // line seeking counts), one whose moves were taken was a scroll or the sheet.
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Final)
                        var dragged = false
                        do {
                            val change = awaitPointerEvent(PointerEventPass.Final).changes.firstOrNull() ?: break
                            if (!change.pressed) break
                            if (change.isConsumed) dragged = true
                        } while (true)
                        if (!dragged) touched()
                    }
                }.nestedScroll(
                    remember {
                        object : NestedScrollConnection {
                            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                                if (consumed.y != 0f) touched()
                                return Offset.Zero
                            }
                        }
                    },
                ).statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Crossfade(targetState = view, animationSpec = tween(300), modifier = Modifier.weight(1f).fillMaxWidth(), label = "classicView") { shown ->
                when (shown) {
                    ClassicView.PLAYER -> PlayerView(state, actions, onSwipeUp = { view = ClassicView.QUEUE })
                    ClassicView.QUEUE -> QueueView(state, actions)
                    ClassicView.LYRICS -> LyricsView(sharedViewModel, state, actions, onClose = lyricsPageClose ?: actions.onDismiss)
                }
            }
            // On the lyrics page only the header above closes it (M3 Expressive's fullscreen lyrics).
            // Before the padding, so the side gutters hold too.
            Column(Modifier.then(if (lyricsPageClose != null) Modifier.keepDragsFromSheet() else Modifier).padding(horizontal = 24.dp)) {
                WavySeekBar(
                    progressFraction = state.sliderValue / 100f,
                    isPlaying = state.controllerState.isPlaying,
                    activeColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    thumbColor = MaterialTheme.colorScheme.primary,
                    onSliderChange = actions.onSliderChange,
                    onSliderChangeFinished = actions.onSliderChangeFinished,
                    modifier = Modifier.fillMaxWidth(),
                )
                val waitingForNetwork by sharedViewModel.waitingForNetwork.collectAsStateWithLifecycle()
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(state.timelineState.elapsedLabel(state.sliderValue / 100f), style = typo().bodySmall, modifier = Modifier.weight(1f))
                    if (waitingForNetwork) {
                        Text(stringResource(Res.string.waiting_for_network), style = typo().bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        state.timelineState.lengthLabel(),
                        style = typo().bodySmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                }
                AnimatedVisibility(
                    visible = view == ClassicView.PLAYER || pillsShown,
                    enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                    exit = shrinkVertically(tween(300)) + fadeOut(tween(200)),
                ) {
                    Column {
                        Spacer(Modifier.height(if (state.isShort) 8.dp else 16.dp))
                        ClassicTransportRow(state, actions)
                        // Takes height from the gap above the cover, lifting everything but the bottom row.
                        if (!state.isShort) Spacer(Modifier.height(RAISE))
                    }
                }
                if (lyricsPageClose == null) {
                    Spacer(Modifier.height(if (state.isShort) 12.dp else 28.dp))
                    BottomRow(state, actions, view, onQueue = { toggle(ClassicView.QUEUE) }, onLyrics = { toggle(ClassicView.LYRICS) })
                }
                Spacer(Modifier.height(if (state.isShort) 8.dp else 12.dp))
            }
        }
    }
}

/** The cover, the current lyric line and the title. A swipe up on the cover opens the queue. */
@Composable
private fun PlayerView(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    onSwipeUp: () -> Unit,
) {
    val isRepeatOne = state.controllerState.repeatState is RepeatState.One
    Column(Modifier.fillMaxSize()) {
        TopRow(state, actions)
        // Cover, lyric line and title stay one group at fixed gaps; the leftover height goes
        // above the cover instead of opening a hole under the line. Where there is too little
        // (a short screen, a large font) the cover gets smaller, not the gaps or the title.
        Column(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
            // A short window has no room for the cover above the controls: a small one goes beside
            // the title, and the lyric line goes (the Lyrics square is one tap away).
            if (!state.isShort) {
                BoxWithConstraints(Modifier.weight(1f, fill = false)) {
                    val side = minOf(maxWidth - GUTTER * 2, maxHeight - COVER_GAP * 2).coerceAtLeast(0.dp)
                    val gutter = (maxWidth - side) / 2
                    CoverPager(state, actions, side, gutter, isRepeatOne, onSwipeUp)
                }
                CoverLyricLine(state, typo().labelSmall, MaterialTheme.colorScheme.onBackground, Modifier.padding(horizontal = GUTTER))
                Spacer(Modifier.height(14.dp))
            }
            TitleRow(state, actions)
            Spacer(Modifier.height(if (state.isShort) 8.dp else 16.dp))
        }
    }
}

/**
 * The cover and its neighbours. The pager spans the screen with the gutters as padding: it clips
 * its pages at its own edges, and an edge at the cover's would cut the cover's shadow square.
 */
@Composable
private fun CoverPager(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    side: Dp,
    gutter: Dp,
    isRepeatOne: Boolean,
    onSwipeUp: () -> Unit,
) {
    HorizontalPager(
        state = state.artworkPagerState,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(side + COVER_GAP * 2)
                // Only an upward drag is taken: a downward one stays the sheet's, to close the
                // player, and a sideways one the pager's, to change track.
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var dy = 0f
                        val drag =
                            awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                                if (over < 0) {
                                    change.consume()
                                    dy = over
                                }
                            } ?: return@awaitEachGesture
                        verticalDrag(drag.id) {
                            dy += it.positionChange().y
                            it.consume()
                        }
                        if (dy < -SWIPE_UP.toPx()) onSwipeUp()
                    }
                },
        contentPadding = PaddingValues(horizontal = gutter, vertical = COVER_GAP),
        // At least the gutter, so a neighbour never peeks in at rest.
        pageSpacing = gutter,
        userScrollEnabled = !isRepeatOne && state.artworkQueue.isNotEmpty(),
        key = { index -> "artwork_${state.artworkQueue.getOrNull(index)?.videoId.orEmpty()}_$index" },
    ) { page ->
        if (page == state.currentOrderIndex) {
            CurrentArtwork(state, actions)
        } else {
            val url = state.artworkQueue.getOrNull(page)?.thumbnails?.maxByOrNull { it.width * it.height }?.url
            ArtworkImage(url = url, modifier = Modifier.fillMaxSize())
        }
    }
}

/** The queue; the song playing, with add to playlist, like and its menu, shows over it while its row is out of sight. */
@Composable
private fun QueueView(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    Column(Modifier.fillMaxSize()) {
        // Lined up with the player's own top row, so the chevron stays put across views.
        QueueTopBar(onClose = actions.onDismiss, modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 8.dp))
        QueueActions()
        QueueList(onMore = actions.onQueueItemMore, bottomInset = 0.dp, modifier = Modifier.weight(1f)) {
            NowPlayingSongHeader(state, actions)
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** The lyrics under the song playing; share sits top right once there are lyrics to share. */
@Composable
private fun LyricsView(
    sharedViewModel: SharedViewModel,
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    onClose: () -> Unit,
) {
    val lyricsData = state.screenData.lyricsData
    var sharing by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(state.dismissIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(stringResource(Res.string.lyrics), style = typo().titleMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
            if (lyricsData != null) {
                IconButton(onClick = { sharing = true }) {
                    Icon(LucidIcons.Share, contentDescription = stringResource(Res.string.share_lyrics), tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                }
            }
        }
        NowPlayingSongHeader(state, actions)
        // A drag on the lines scrolls them and never pulls the player down: only the header does.
        ClassicLyricsBody(sharedViewModel, onOpenLyricsSettings = actions.onOpenLyricsSettings, modifier = Modifier.weight(1f).keepDragsFromSheet())
    }
    if (sharing && lyricsData != null) {
        ShareLyricsSheet(
            lines = lyricsData.toShareLyricsLines(),
            songTitle = state.screenData.nowPlayingTitle,
            artistName = state.screenData.artistName,
            artwork = state.screenData.bitmap,
            seedColor = state.startColor.value,
            initialLineIndex = state.currentLyricLineIndex,
            onDismiss = { sharing = false },
        )
    }
}

/**
 * Drags here never reach the sheet the page sits in: a list inside scrolls and stops at its ends,
 * and a vertical drag on anything else is taken before the sheet sees it.
 */
private fun Modifier.keepDragsFromSheet(): Modifier =
    nestedScroll(KeepScrollFromSheet)
        .pointerInput(Unit) { detectVerticalDragGestures { change, _ -> change.consume() } }

private object KeepScrollFromSheet : NestedScrollConnection {
    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset = available

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = available
}

@Composable
private fun NowPlayingSongHeader(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    SongHeader(
        thumbnail = state.screenData.thumbnailURL,
        title = state.screenData.nowPlayingTitle,
        artist = state.screenData.artistName,
        liked = state.controllerState.isLiked,
        onLike = { actions.onUIEvent(UIEvent.ToggleLike) },
        onMore = actions.onShowMoreSheet,
        onAddToPlaylist = actions.onShowAddToPlaylist,
    )
}

/**
 * [thumbnail] blurred to fill its box, under a wash of [wash] so text over it stays readable. The
 * blur needs Android 12 ([isLyricsBlurSupported]); callers check before drawing this.
 */
@Composable
internal fun FrostedCover(
    thumbnail: String?,
    wash: Color,
    washFrom: Float = 0.5f,
    washTo: Float = 0.75f,
) {
    Crossfade(targetState = thumbnail, animationSpec = tween(600), label = "frostedCover") { thumbnail ->
        var url by remember(thumbnail) { mutableStateOf(thumbnail) }
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(url)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    // Blurred this hard, a small decode looks the same and spares a full-screen bitmap.
                    .size(FROST_DECODE_PX)
                    .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onError = {
                val fallback = url?.replace("maxresdefault", "hqdefault")
                if (fallback != null && fallback != url) url = fallback
            },
            modifier = Modifier.fillMaxSize().blur(FROST_BLUR, BlurredEdgeTreatment.Rectangle),
        )
    }
    // The wash's alpha top to bottom; picked by eye on the phone.
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to wash.copy(alpha = washFrom), 1f to wash.copy(alpha = washTo))))
}

@Composable
private fun TopRow(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = GUTTER, end = GUTTER, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = actions.onDismiss) {
            Icon(state.dismissIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        }
        val remoteDevice = rememberRemoteDevice()
        // Song | Video takes the middle on a video, or a song with another version, as in YouTube Music.
        if (state.canSwitchVersion && remoteDevice == null) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                VersionSwitch(isVideo = state.screenData.isVideo && state.shouldShowVideo, onSwitch = actions.onSwitchVersion)
            }
        } else Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            if (remoteDevice != null) {
                PlayingOnLabel(remoteDevice, playing = state.controllerState.isPlaying, style = typo().bodyMedium)
            } else {
                Text(stringResource(Res.string.now_playing), style = typo().bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                text = state.screenData.playlistName,
                style = typo().bodyMedium,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.basicMarquee(),
            )
        }
        OutputButton(state)
    }
}

/** Where the sound goes, lit while it isn't the phone's own speaker. */
@Composable
private fun OutputButton(state: NowPlayingContentState) {
    val outputs by koinInject<MediaPlayerHandler>().player.audioOutputs.collectAsStateWithLifecycle()
    val active = outputs.firstOrNull { it.isActive }
    val away = state.castState.isRemote || rememberRemoteDevice() != null || (active != null && active.kind != AudioOutputKind.DEVICE_SPEAKER)
    var choosing by remember { mutableStateOf(false) }
    IconButton(onClick = { choosing = true }) {
        Icon(
            LucidIcons.Headphones,
            contentDescription = null,
            tint = if (away) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
    }
    if (choosing) {
        AudioOutputSheetHost(castState = state.castState, onDismiss = { choosing = false })
    }
}

/** The track now playing: its artwork, or its video when one is playing. */
@Composable
private fun CurrentArtwork(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    val showVideo = state.screenData.isVideo && state.shouldShowVideo
    // maxresdefault only exists for HD videos; hqdefault always does.
    var artworkUrl by remember(state.screenData.thumbnailURL) { mutableStateOf(state.screenData.thumbnailURL) }
    Box(
        Modifier.fillMaxSize().doubleTapToLike(
            state.controllerState.isLiked,
            onLike = { actions.onUIEvent(UIEvent.ToggleLike) },
            onLongPress = actions.onShowMoreSheet,
        ),
        contentAlignment = Alignment.Center,
    ) {
        if (!showVideo) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(artworkUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        // A fixed memory key, used as the placeholder too: when a queue reorder
                        // (shuffle) rebuilds the pager page, the cover redraws from memory at once
                        // instead of flashing the placeholder.
                        .memoryCacheKey(artworkUrl + "BIGGER")
                        .placeholderMemoryCacheKey(artworkUrl + "BIGGER")
                        .crossfade(400)
                        .build(),
                contentDescription = null,
                onSuccess = { actions.onArtworkBitmap(it.result.image.toImageBitmap()) },
                onError = {
                    val fallback = artworkUrl?.replace("maxresdefault", "hqdefault")
                    if (fallback != null && fallback != artworkUrl) artworkUrl = fallback
                },
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier =
                    Modifier
                        .fillMaxSize()
                        .shadow(16.dp, ArtworkShape, spotColor = state.spotShadowColor)
                        .clip(ArtworkShape),
            )
        }
        AnimatedVisibility(visible = showVideo) {
            Box(
                Modifier
                    .aspectRatio(state.videoAspectRatio)
                    .clip(ArtworkShape)
                    .background(Color.Black),
            ) {
                MediaPlayerViewWithSubtitle(
                    playerName = MAIN_PLAYER,
                    modifier = Modifier.align(Alignment.Center),
                    shouldShowSubtitle = false,
                    shouldPip = false,
                    shouldScaleDownSubtitle = true,
                    timelineState = state.timelineState,
                    lyricsData = state.screenData.lyricsData?.lyrics,
                    translatedLyricsData = state.screenData.lyricsData?.translatedLyrics?.first,
                    isInPipMode = state.isInPipMode,
                    mainTextStyle = typo().bodyLarge,
                    translatedTextStyle = typo().bodyMedium,
                )
                IconButton(onClick = actions.onEnterFullscreenVideo, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(LucidIcons.Fullscreen, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ArtworkImage(
    url: String?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(url)
                .diskCachePolicy(CachePolicy.ENABLED)
                .diskCacheKey(url)
                .memoryCacheKey(url)
                .placeholderMemoryCacheKey(url)
                .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = rememberHolderPainter(),
        error = rememberHolderPainter(),
        modifier = modifier.clip(ArtworkShape),
    )
}

/** Download / downloaded / remove, right beside the title where people look for it. */
@Composable
private fun TitleDownloadButton() {
    val sharedViewModel: SharedViewModel = koinInject()
    val downloadState by sharedViewModel.nowPlayingDownloadState.collectAsStateWithLifecycle()
    val downloaded = downloadState == DownloadState.STATE_DOWNLOADED
    val downloading = downloadState == DownloadState.STATE_DOWNLOADING
    IconButton(onClick = sharedViewModel::toggleNowPlayingDownload, modifier = Modifier.size(40.dp)) {
        if (downloading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
        } else {
            Icon(
                if (downloaded) LucidIcons.Check else LucidIcons.Download,
                contentDescription = stringResource(if (downloaded) Res.string.downloaded else Res.string.download),
                tint = if (downloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
private fun TitleRow(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    val liked = state.controllerState.isLiked
    Row(Modifier.padding(horizontal = GUTTER), verticalAlignment = Alignment.CenterVertically) {
        if (state.isShort) {
            CoverThumb(state, actions)
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            // Like sits on the title's line, not between title and artist.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.screenData.nowPlayingTitle,
                    style = typo().titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    modifier = Modifier.weight(1f).basicMarquee().clickable(onClick = actions.onNavigateToAlbum),
                )
                TitleDownloadButton()
                IconButton(onClick = { actions.onUIEvent(UIEvent.ToggleLike) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        if (liked) LucidIcons.Favorite else LucidIcons.FavoriteBorder,
                        contentDescription = null,
                        tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Text(
                text = state.screenData.artistName,
                style = typo().bodyLarge,
                maxLines = 1,
                modifier =
                    Modifier
                        .basicMarquee()
                        .clickable(onClick = actions.onNavigateToArtist),
            )
        }
    }
}

/**
 * The cover beside the title on a short window, in every style. It feeds the palette, as the big
 * one it stands in for does.
 */
@Composable
internal fun CoverThumb(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    size: Dp = 56.dp,
    shape: Shape = SmallShape,
) {
    var url by remember(state.screenData.thumbnailURL) { mutableStateOf(state.screenData.thumbnailURL) }
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(url)
                .diskCachePolicy(CachePolicy.ENABLED)
                // Full size, not the thumbnail's: the lyrics and song share cards draw this bitmap.
                .size(Size.ORIGINAL)
                .crossfade(400)
                .build(),
        contentDescription = null,
        onSuccess = { actions.onArtworkBitmap(it.result.image.toImageBitmap()) },
        onError = {
            val fallback = url?.replace("maxresdefault", "hqdefault")
            if (fallback != null && fallback != url) url = fallback
        },
        contentScale = ContentScale.Crop,
        placeholder = rememberHolderPainter(),
        error = rememberHolderPainter(),
        modifier =
            Modifier
                .size(size)
                .clip(shape)
                .doubleTapToLike(
                    state.controllerState.isLiked,
                    onLike = { actions.onUIEvent(UIEvent.ToggleLike) },
                    onLongPress = actions.onShowMoreSheet,
                ),
    )
}

/** Previous, play / pause, next: three pills in the cover's colour, play the widest. */
@Composable
internal fun ClassicTransportRow(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
) {
    val control = state.controllerState
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // A short window gets lower pills, the same width.
        val side = Modifier.width(80.dp).height(if (state.isShort) 48.dp else 64.dp)
        val sideIcon = if (state.isShort) 28.dp else 32.dp
        FilledButton(LucidIcons.SkipPrevious, side, CircleShape, iconSize = sideIcon, enabled = control.isPreviousAvailable) {
            actions.onUIEvent(UIEvent.Previous)
        }
        FilledButton(
            if (control.isPlaying) LucidIcons.Pause else LucidIcons.PlayArrow,
            Modifier.width(120.dp).height(if (state.isShort) 56.dp else 80.dp),
            CircleShape,
            iconSize = if (state.isShort) 34.dp else 42.dp,
        ) { actions.onUIEvent(UIEvent.PlayPause) }
        FilledButton(LucidIcons.SkipNext, side, CircleShape, iconSize = sideIcon, enabled = control.isNextAvailable) {
            actions.onUIEvent(UIEvent.Next)
        }
    }
}

/**
 * Add to playlist, repeat, Lyrics, shuffle, Queue as outlined squares; the song menu on the right.
 * Queue and Lyrics light up while their view is on screen. The sleep timer is in the menu.
 */
@Composable
private fun BottomRow(
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    view: ClassicView,
    onQueue: () -> Unit,
    onLyrics: () -> Unit,
) {
    val control = state.controllerState
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(LucidIcons.PlaylistAdd, onClick = actions.onShowAddToPlaylist)
        OutlinedButton(
            if (control.repeatState is RepeatState.One) LucidIcons.RepeatOne else LucidIcons.Repeat,
            active = control.repeatState !is RepeatState.None,
        ) { actions.onUIEvent(UIEvent.Repeat) }
        OutlinedButton(LucidIcons.Lyrics, active = view == ClassicView.LYRICS, onClick = onLyrics)
        OutlinedButton(LucidIcons.Shuffle, active = control.isShuffle) { actions.onUIEvent(UIEvent.Shuffle) }
        OutlinedButton(LucidIcons.QueueMusic, active = view == ClassicView.QUEUE, onClick = onQueue)
        Spacer(Modifier.weight(1f))
        FilledButton(LucidIcons.MoreVert, Modifier.size(46.dp), CircleShape, onClick = actions.onShowMoreSheet)
    }
}

/** Filled with the cover's colour: the transport pills, the song menu. */
@Composable
private fun FilledButton(
    icon: ImageVector,
    modifier: Modifier,
    shape: Shape,
    iconSize: Dp = 24.dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.4f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(iconSize))
    }
}

/** A small outlined square; [active] lights it in the cover's colour (shuffle on, a timer running). */
@Composable
private fun OutlinedButton(
    icon: ImageVector,
    active: Boolean = false,
    onClick: () -> Unit,
) {
    val color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .size(44.dp)
            .clip(SmallShape)
            .border(1.dp, color.copy(alpha = if (active) 0.9f else 0.3f), SmallShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}
