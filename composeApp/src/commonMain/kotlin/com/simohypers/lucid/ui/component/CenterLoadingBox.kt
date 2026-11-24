package com.simohypers.lucid.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simohypers.lucid.ui.theme.typo
import com.simohypers.lucid.viewModel.base.BaseViewModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CenterLoadingBox(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        LucidLoader(Modifier.size(48.dp))
    }
}

/** The arc's circumference in the 200 viewport (r = 80), against which its 60 to 300 long dash is measured. */
private const val ARC_LENGTH = 502.65f

/**
 * Generic loader: the rind as an arc that spins and grows and shrinks.
 */
@Composable
fun LucidLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val transition = rememberInfiniteTransition(label = "loader")
    val turn = transition.animateFloat(0f, 360f, infiniteRepeatable(tween(1100, easing = LinearEasing)), label = "turn")
    val phase = transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "phase")
    Spacer(
        modifier.progressSemantics().drawBehind {
            val s = size.minDimension / 200f
            val p = phase.value
            val swell = EaseInOut.transform(if (p < .5f) p * 2 else 2 - p * 2)
            drawArc(
                color = color,
                startAngle = turn.value,
                sweepAngle = (60f + 240f * swell) / ARC_LENGTH * 360f,
                useCenter = false,
                topLeft = Offset(center.x - 80 * s, center.y - 80 * s),
                size = Size(160 * s, 160 * s),
                style = Stroke(18 * s, cap = StrokeCap.Round),
            )
        },
    )
}

/**
 * Says what the app is fetching behind a menu that has already closed, such as Starting radio.
 * Only a wait that lasts shows, so a quick one doesn't flash.
 */
@Composable
fun BusyPill(modifier: Modifier = Modifier) {
    val label = BaseViewModel.busyLabels.collectAsStateWithLifecycle().value.lastOrNull()
    var visible by remember { mutableStateOf(false) }
    // Kept while the pill fades out.
    var text by remember { mutableStateOf<StringResource?>(null) }
    LaunchedEffect(label) {
        if (label == null) {
            visible = false
            return@LaunchedEffect
        }
        if (!visible) delay(300)
        text = label
        visible = true
    }
    AnimatedVisibility(visible, modifier, enter = fadeIn() + slideInVertically { it / 2 }, exit = fadeOut()) {
        Row(
            Modifier
                .shadow(6.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LucidLoader(Modifier.size(20.dp))
            text?.let { Text(stringResource(it), style = typo().labelMedium, color = MaterialTheme.colorScheme.onSurface) }
        }
    }
}
