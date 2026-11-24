package com.simohypers.lucid.ui.screen.player.content.applemusic

import com.simohypers.lucid.ui.theme.rounded
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simohypers.lucid.domain.data.player.AudioOutput
import com.simohypers.lucid.domain.data.player.AudioOutputKind
import com.simohypers.lucid.domain.data.player.GenericCastState
import com.simohypers.lucid.domain.mediaservice.handler.MediaPlayerHandler
import com.simohypers.lucid.Platform
import com.simohypers.lucid.expect.ui.DeviceVolumeController
import com.simohypers.lucid.expect.ui.rememberCastReceivers
import com.simohypers.lucid.getPlatform
import com.simohypers.lucid.ui.component.SurfaceDarkColors
import com.simohypers.lucid.ui.component.rememberSurfaceDarkColors
import com.simohypers.lucid.ui.icon.Check
import com.simohypers.lucid.ui.icon.Headphones
import com.simohypers.lucid.ui.icon.MusicCast
import com.simohypers.lucid.ui.icon.LucidIcons
import com.simohypers.lucid.ui.icon.Smartphone
import com.simohypers.lucid.ui.icon.Speaker
import com.simohypers.lucid.ui.icon.Computer
import com.simohypers.lucid.ui.theme.typo
import com.simohypers.lucid.domain.repository.DeviceLinkRepository
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.audio_output
import lucid.composeapp.generated.resources.audio_output_bluetooth
import lucid.composeapp.generated.resources.audio_output_cast
import lucid.composeapp.generated.resources.audio_output_hdmi
import lucid.composeapp.generated.resources.audio_output_playing_here
import lucid.composeapp.generated.resources.audio_output_system_default
import lucid.composeapp.generated.resources.audio_output_usb
import lucid.composeapp.generated.resources.audio_output_wired
import lucid.composeapp.generated.resources.devices
import lucid.composeapp.generated.resources.devices_offline
import lucid.composeapp.generated.resources.devices_online
import lucid.composeapp.generated.resources.devices_paused
import lucid.composeapp.generated.resources.devices_playing

/**
 * [AppleMusicOutputSheet] wired straight to the player, for the Now Playing styles that only need
 * the output list inside the sheet (Classic opens it from its headphones button). No volume row:
 * the owner doesn't want a volume slider in the app.
 */
@Composable
internal fun AudioOutputSheetHost(
    castState: GenericCastState,
    onDismiss: () -> Unit,
) {
    val player = koinInject<MediaPlayerHandler>().player
    val audioOutputs by player.audioOutputs.collectAsStateWithLifecycle()
    AppleMusicOutputSheet(
        outputs = audioOutputs,
        castState = castState,
        deviceVolumeController = null,
        onSelectOutput = { output -> player.selectAudioOutput(output.id) },
        onRefresh = { player.refreshAudioOutputs() },
        onDismiss = onDismiss,
    )
}

/**
 * Where the sound goes — opened from the headphones half of the action row's capsule.
 *
 * The phone's own outputs and the Cast receivers are one list, the way Apple lists AirPlay speakers
 * beside the iPhone: they answer the same question, so they belong in the same place. Picking a
 * local output while casting ends the Cast session first. Built in the app's own sheet style (the
 * same card, handle and rows as the Now Playing ⋯ sheet), not a new one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppleMusicOutputSheet(
    outputs: List<AudioOutput>,
    castState: GenericCastState,
    deviceVolumeController: DeviceVolumeController?,
    onSelectOutput: (AudioOutput) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = rememberSurfaceDarkColors()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // A pick is the whole job: the sheet slides away and the caption under the button names the new
    // output. onDismiss runs however the slide ends — waiting for a finished hide is how a sheet gets
    // left parked half-way with no scrim to close it.
    val closeSheet: () -> Unit = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } }
    val localDensity = LocalDensity.current
    // Scanning for receivers only while this is open: discovery keeps the radio busy.
    val cast = rememberCastReceivers(discover = true)
    // Devices coming and going update the list on their own; the system's own route changing does not.
    LaunchedEffect(Unit) { onRefresh() }
    // Linked Lucid devices, Spotify Connect style: while one plays, the phone's outputs aren't.
    val deviceLink = koinInject<DeviceLinkRepository>()
    val link by deviceLink.link.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = .5f),
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = rounded(topStart = 8.dp, topEnd = 8.dp),
            colors = CardDefaults.cardColors().copy(containerColor = colors.container),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier =
                        Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colors.handle),
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(Res.string.audio_output),
                    style = typo().labelMedium,
                    color = colors.content,
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 6.dp),
                )
                outputs.forEach { output ->
                    val playingHere = output.isActive && !castState.isRemote && !link.controlling
                    OutputRow(
                        icon = output.icon(),
                        name = output.displayName(),
                        status = if (playingHere) stringResource(Res.string.audio_output_playing_here) else output.kindLabel(),
                        selected = playingHere,
                        colors = colors,
                        onClick = {
                            // Coming back from a receiver: end the Cast session first, or the pick
                            // would route a local player that is not the one playing.
                            if (castState.isRemote) cast.disconnect()
                            // Picking this phone's sound while the computer plays brings the music here.
                            if (link.controlling) deviceLink.playOn(null)
                            onSelectOutput(output)
                            closeSheet()
                        },
                    )
                }
                if (cast.receivers.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.10f),
                    )
                    Text(
                        text = stringResource(Res.string.audio_output_cast),
                        style = typo().bodySmall,
                        color = colors.subtitle,
                        modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 20.dp, top = 4.dp),
                    )
                    cast.receivers.forEach { receiver ->
                        OutputRow(
                            icon = LucidIcons.MusicCast,
                            name = receiver.name,
                            status = if (receiver.isConnected) stringResource(Res.string.audio_output_playing_here) else null,
                            selected = receiver.isConnected,
                            colors = colors,
                            onClick = { if (!receiver.isConnected) cast.connect(receiver.id) },
                        )
                    }
                }
                if (link.paired && link.devices.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.10f),
                    )
                    Text(
                        text = stringResource(Res.string.devices),
                        style = typo().bodySmall,
                        color = colors.subtitle,
                        modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 20.dp, top = 4.dp),
                    )
                    link.devices.forEach { device ->
                        val isPlayer = device.id == link.player?.id
                        val selected = isPlayer && link.controlling
                        OutputRow(
                            icon = LucidIcons.Computer,
                            name = device.name,
                            status =
                                stringResource(
                                    when {
                                        !device.online -> Res.string.devices_offline
                                        isPlayer && link.isPlaying -> Res.string.devices_playing
                                        isPlayer -> Res.string.devices_paused
                                        else -> Res.string.devices_online
                                    },
                                ),
                            selected = selected,
                            colors = colors,
                            onClick = {
                                if (!selected && device.online) {
                                    deviceLink.playOn(device.id)
                                    closeSheet()
                                }
                            },
                        )
                    }
                }
                deviceVolumeController?.let { controller ->
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.10f),
                    )
                    AppleMusicVolumeRow(controller = controller, modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp))
                }
                Spacer(
                    modifier =
                        Modifier.height(
                            with(localDensity) { WindowInsets.systemBars.getBottom(localDensity).toDp() } + 12.dp,
                        ),
                )
            }
        }
    }
}

// One output: the ⋯ sheet's ActionButton metrics (48dp icon box, labelSmall), with a status line
// under the name and a tick on the one playing.
@Composable
private fun OutputRow(
    icon: ImageVector,
    name: String,
    status: String?,
    selected: Boolean,
    colors: SurfaceDarkColors,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.content, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(text = name, style = typo().labelSmall, color = colors.content, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (status != null) {
                Text(text = status, style = typo().bodySmall, color = colors.subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        AnimatedVisibility(visible = selected, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
            Icon(imageVector = LucidIcons.Check, contentDescription = null, tint = colors.content, modifier = Modifier.size(24.dp))
        }
    }
}

/**
 * What to call an output. A backend leaves the name blank when all it has is meaningless — the
 * desktop's "auto", or a wire reporting the phone's own model — and the kind names it instead.
 */
@Composable
internal fun AudioOutput.displayName(): String =
    name.ifBlank {
        when (kind) {
            AudioOutputKind.DEVICE_SPEAKER -> stringResource(Res.string.audio_output_system_default)
            AudioOutputKind.WIRED -> stringResource(Res.string.audio_output_wired)
            AudioOutputKind.BLUETOOTH -> stringResource(Res.string.audio_output_bluetooth)
            AudioOutputKind.USB -> stringResource(Res.string.audio_output_usb)
            AudioOutputKind.HDMI -> stringResource(Res.string.audio_output_hdmi)
            AudioOutputKind.OTHER -> stringResource(Res.string.audio_output_system_default)
        }
    }

// The second line under a device that is not playing: what kind of connection it is, when the name
// alone does not say so.
@Composable
private fun AudioOutput.kindLabel(): String? =
    when (kind) {
        AudioOutputKind.BLUETOOTH -> stringResource(Res.string.audio_output_bluetooth)
        AudioOutputKind.USB -> if (name.isNotBlank()) stringResource(Res.string.audio_output_usb) else null
        AudioOutputKind.HDMI -> if (name.isNotBlank()) stringResource(Res.string.audio_output_hdmi) else null
        else -> null
    }

private fun AudioOutput.icon(): ImageVector =
    when (kind) {
        // The phone's own speaker on Android; the system default on desktop, which is a computer.
        AudioOutputKind.DEVICE_SPEAKER -> if (getPlatform() == Platform.Android) LucidIcons.Smartphone else LucidIcons.Speaker
        AudioOutputKind.WIRED, AudioOutputKind.BLUETOOTH, AudioOutputKind.USB -> LucidIcons.Headphones
        AudioOutputKind.HDMI, AudioOutputKind.OTHER -> LucidIcons.Speaker
    }
