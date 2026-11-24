package com.simohypers.lucid.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.simohypers.lucid.ui.theme.LocalAppColors

/**
 * Theme-aware artwork placeholder skeleton loader.
 */
@Composable
fun rememberHolderPainter(@Suppress("UNUSED_PARAMETER") isVideo: Boolean = false): Painter {
    return ColorPainter(LocalAppColors.current.shimmerBackground)
}
