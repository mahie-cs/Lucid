package com.simohypers.lucid.ui.screen.player.content.applemusic

import com.simohypers.lucid.extension.ParsedRichSyncLine
import com.simohypers.lucid.extension.WordTiming
import kotlin.test.Test
import kotlin.test.assertEquals

class SpreadOverTest {
    private val line = ParsedRichSyncLine(listOf(WordTiming("夜に", 1_000), WordTiming("駆ける", 1_400)), 900, 2_000)

    @Test
    fun theReadingSharesTheSungSpanByLength() {
        // 12 letters over the 1000 ms from the first word to the line's end, extra spaces ignored.
        val spread = line.spreadOver(" yoru  ni kakeru")
        assertEquals(listOf("yoru" to 1_000L, "ni" to 1_333L, "kakeru" to 1_500L), spread.words.map { it.text to it.startTimeMs })
        assertEquals(900, spread.lineStartTimeMs)
        assertEquals(2_000, spread.lineEndTimeMs)
    }

    @Test
    fun noLineEndGivesTheLastWordItsFallback() {
        val open = line.copy(lineEndTimeMs = Long.MAX_VALUE)
        assertEquals(1_400 + 600, open.spreadOver("yoru ni").lineEndTimeMs)
    }
}
