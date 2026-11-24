package com.simohypers.lucid.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.simohypers.lucid.ui.icon.ArrowBackIosNew
import com.simohypers.lucid.ui.icon.LucidIcons
import com.simohypers.lucid.ui.theme.typo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.withTimeout
import lucid.composeapp.generated.resources.Res
import lucid.composeapp.generated.resources.retry
import lucid.composeapp.generated.resources.terms_link
import lucid.composeapp.generated.resources.terms_load_failed
import org.jetbrains.compose.resources.stringResource

/** Where the terms live. HEAD is the repository's default branch, so a rename of it breaks nothing. */
private const val TERMS_URL = "https://raw.githubusercontent.com/mahie-cs/Lucid/HEAD/TERMS.md"

/** Fetches TERMS.md from GitHub. The last copy fetched is kept for the rest of the session. */
private object TermsSource {
    private val http by lazy { HttpClient(CIO) }
    private var cached: String? = null

    suspend fun load(): String {
        cached?.let { return it }
        val text = withTimeout(15_000) { http.get(TERMS_URL).bodyAsText() }
        check(text.isNotBlank()) { "empty terms" }
        cached = text
        return text
    }
}

private sealed interface TermsState {
    data object Loading : TermsState

    data object Failed : TermsState

    data class Loaded(
        val blocks: List<MdBlock>,
    ) : TermsState
}

/** The Terms and Conditions page, read from GitHub and drawn here: nothing opens in a browser. */
@Composable
fun TermsScreen(onBack: () -> Unit) {
    var attempt by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf<TermsState>(TermsState.Loading) }
    LaunchedEffect(attempt) {
        state = TermsState.Loading
        state =
            runCatching { TermsSource.load() }
                .map { TermsState.Loaded(parseMarkdown(it)) as TermsState }
                .getOrElse { TermsState.Failed }
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(LucidIcons.ArrowBackIosNew, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
            }
            Text(stringResource(Res.string.terms_link), style = typo().titleMedium, color = MaterialTheme.colorScheme.onBackground)
        }
        when (val shown = state) {
            TermsState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            TermsState.Failed ->
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(Res.string.terms_load_failed), style = typo().bodyMedium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { attempt++ }) { Text(stringResource(Res.string.retry)) }
                }
            is TermsState.Loaded -> TermsBody(shown.blocks)
        }
    }
}

@Composable
private fun TermsBody(blocks: List<MdBlock>) {
    val linkColor = MaterialTheme.colorScheme.primary
    SelectionContainer {
        LazyColumn(
            Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(blocks) { block ->
                when (block) {
                    is MdBlock.Heading ->
                        Text(
                            text = inlineMarkdown(block.text, linkColor),
                            style = if (block.level <= 1) typo().titleLarge else typo().titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = if (block.level <= 1) 0.dp else 8.dp),
                        )
                    is MdBlock.Paragraph -> Text(inlineMarkdown(block.text, linkColor), style = typo().bodyMedium)
                    is MdBlock.Bullet ->
                        Row {
                            Text("\u2022", style = typo().bodyMedium, modifier = Modifier.padding(end = 10.dp))
                            Text(inlineMarkdown(block.text, linkColor), style = typo().bodyMedium, modifier = Modifier.weight(1f))
                        }
                    MdBlock.Rule -> HorizontalDivider()
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/** The few shapes TERMS.md is written in: headings, paragraphs, bullets and rules. */
internal sealed interface MdBlock {
    data class Heading(
        val level: Int,
        val text: String,
    ) : MdBlock

    data class Paragraph(
        val text: String,
    ) : MdBlock

    data class Bullet(
        val text: String,
    ) : MdBlock

    data object Rule : MdBlock
}

internal fun parseMarkdown(markdown: String): List<MdBlock> {
    val blocks = mutableListOf<MdBlock>()
    val paragraph = StringBuilder()
    var bullet: StringBuilder? = null

    fun flush() {
        if (paragraph.isNotBlank()) blocks += MdBlock.Paragraph(paragraph.toString().trim())
        paragraph.clear()
        bullet?.let { blocks += MdBlock.Bullet(it.toString().trim()) }
        bullet = null
    }
    for (raw in markdown.replace("\r\n", "\n").lines()) {
        val line = raw.trimEnd()
        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> flush()
            trimmed.startsWith("#") -> {
                flush()
                val level = trimmed.takeWhile { it == '#' }.length
                blocks += MdBlock.Heading(level, trimmed.drop(level).trim())
            }
            trimmed == "---" || trimmed == "***" -> {
                flush()
                blocks += MdBlock.Rule
            }
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                flush()
                bullet = StringBuilder(trimmed.drop(2))
            }
            // A wrapped line of the bullet above.
            bullet != null && line.startsWith(" ") -> bullet!!.append(' ').append(trimmed)
            else -> {
                bullet?.let {
                    blocks += MdBlock.Bullet(it.toString().trim())
                    bullet = null
                }
                if (paragraph.isNotEmpty()) paragraph.append(' ')
                paragraph.append(trimmed)
            }
        }
    }
    flush()
    return blocks
}

/** **bold**, [text](url) and <url>, drawn as text: links are shown, not followed. */
internal fun inlineMarkdown(
    source: String,
    linkColor: Color,
): AnnotatedString =
    buildAnnotatedString {
        val link = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
        var i = 0
        while (i < source.length) {
            when {
                source.startsWith("**", i) && source.indexOf("**", i + 2) > 0 -> {
                    val end = source.indexOf("**", i + 2)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(source.substring(i + 2, end)) }
                    i = end + 2
                }
                source[i] == '[' && source.indexOf("](", i) > 0 && source.indexOf(')', source.indexOf("](", i)) > 0 -> {
                    val mid = source.indexOf("](", i)
                    val end = source.indexOf(')', mid)
                    withStyle(link) { append(source.substring(i + 1, mid)) }
                    i = end + 1
                }
                source[i] == '<' && source.indexOf('>', i) > 0 && source.startsWith("http", i + 1) -> {
                    val end = source.indexOf('>', i)
                    withStyle(link) { append(source.substring(i + 1, end)) }
                    i = end + 1
                }
                else -> {
                    append(source[i])
                    i++
                }
            }
        }
    }
