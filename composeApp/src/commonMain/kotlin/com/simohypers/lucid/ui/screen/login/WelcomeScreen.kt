package com.simohypers.lucid.ui.screen.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simohypers.lucid.ui.theme.typo
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.continue_with_google
import lucid.composeapp.generated.resources.terms_link
import lucid.composeapp.generated.resources.terms_prefix
import lucid.composeapp.generated.resources.terms_suffix
import lucid.composeapp.generated.resources.welcome_1
import lucid.composeapp.generated.resources.welcome_2
import lucid.composeapp.generated.resources.welcome_3
import lucid.composeapp.generated.resources.welcome_4
import lucid.composeapp.generated.resources.welcome_5
import lucid.composeapp.generated.resources.welcome_6
import lucid.composeapp.generated.resources.welcome_7
import lucid.composeapp.generated.resources.welcome_8
import lucid.composeapp.generated.resources.welcome_9
import lucid.composeapp.generated.resources.welcome_tagline
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** How far the field of pictures is turned. Its rows run at 60 degrees to the screen's edge, the pictures lean 30. */
private const val TILT_DEGREES = 30f

/**
 * The page a signed-out app opens on. Pictures roll up the top half at 60 degrees, each tilted 30,
 * and fade into the page; under them is the one way in, Continue with Google, a line about the
 * app and the terms. Signing in is the sign-in page's job: it comes back here, the app sees the
 * session and moves to Home.
 */
@Composable
fun WelcomeScreen(
    onContinueWithGoogle: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    val background = MaterialTheme.colorScheme.background
    Box(Modifier.fillMaxSize().background(background)) {
        Box(Modifier.fillMaxWidth().fillMaxHeight(0.66f)) {
            RollingPictures(Modifier.fillMaxSize())
            // Clear for the first third, then into the page's own colour, so the pictures are gone halfway down.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.3f to Color.Transparent, 1f to background)),
            )
        }
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Lucid", style = typo().titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.welcome_tagline),
                style = typo().bodySmall.copy(fontSize = 12.sp),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onContinueWithGoogle,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Text(stringResource(Res.string.continue_with_google), style = typo().titleMedium)
            }
            Spacer(Modifier.height(16.dp))
            TermsLine(onOpenTerms)
        }
    }
}

@Composable
private fun TermsLine(onOpenTerms: () -> Unit) {
    val prefix = stringResource(Res.string.terms_prefix)
    val link = stringResource(Res.string.terms_link)
    val suffix = stringResource(Res.string.terms_suffix)
    val text =
        buildAnnotatedString {
            append(prefix)
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = MaterialTheme.colorScheme.onBackground)) { append(link) }
            append(suffix)
        }
    Text(
        text = text,
        style = typo().bodySmall.copy(fontSize = 11.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.clickable(onClick = onOpenTerms),
    )
}

private val CARD_WIDTH = 120.dp
private val CARD_HEIGHT = 273.dp
private val CARD_GAP = 12.dp

/** Pictures in the four-card loop each column rolls through; shown twice so the loop never shows its seam. */
private const val LOOP_CARDS = 4

/**
 * A field of columns of pictures, turned [TILT_DEGREES] clockwise, rolling up its own length: on
 * the screen that is up and to the right at 60 degrees, the pictures leaning 30. The field is
 * larger than its window so the turned corners still cover it, and the window clips it.
 */
@Composable
private fun RollingPictures(modifier: Modifier = Modifier) {
    // Each picture is decoded once; the cards share these.
    val pictures =
        listOf(
            painterResource(Res.drawable.welcome_1),
            painterResource(Res.drawable.welcome_2),
            painterResource(Res.drawable.welcome_3),
            painterResource(Res.drawable.welcome_4),
            painterResource(Res.drawable.welcome_5),
            painterResource(Res.drawable.welcome_6),
            painterResource(Res.drawable.welcome_7),
            painterResource(Res.drawable.welcome_8),
            painterResource(Res.drawable.welcome_9),
        )
    val progress by rememberInfiniteTransition(label = "roll").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 36_000, easing = LinearEasing), RepeatMode.Restart),
        label = "rollProgress",
    )
    val loopPx = with(LocalDensity.current) { ((CARD_HEIGHT + CARD_GAP) * LOOP_CARDS).toPx() }
    BoxWithConstraints(modifier.clipToBounds()) {
        val fieldWidth = maxWidth * 1.7f
        val fieldHeight = maxHeight * 1.9f
        val columns = (fieldWidth / (CARD_WIDTH + CARD_GAP)).toInt() + 1
        Row(
            Modifier
                .align(Alignment.Center)
                .requiredSize(fieldWidth, fieldHeight)
                .graphicsLayer { rotationZ = TILT_DEGREES },
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(CARD_GAP, Alignment.CenterHorizontally),
        ) {
            repeat(columns) { column ->
                Column(
                    Modifier
                        .width(CARD_WIDTH)
                        .fillMaxHeight()
                        .clipToBounds(),
                ) {
                    Column(
                        Modifier
                            .wrapContentHeight(align = Alignment.Top, unbounded = true)
                            .graphicsLayer {
                                // Columns start at different points of the loop, so the field isn't a grid.
                                translationY = -(progress * loopPx) - (column % 3) * loopPx / 3f
                            },
                    ) {
                        repeat(LOOP_CARDS * 3) { k ->
                            Image(
                                painter = pictures[(column * 2 + k % LOOP_CARDS) % pictures.size],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .padding(bottom = CARD_GAP)
                                        .size(CARD_WIDTH, CARD_HEIGHT)
                                        .clip(RoundedCornerShape(18.dp)),
                            )
                        }
                    }
                }
            }
        }
    }
}
