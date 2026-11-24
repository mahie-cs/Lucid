package com.simohypers.lucid.domain.data.model.lyrics

import com.simohypers.lucid.domain.data.entities.LyricsEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LyricsRecheckTest {
    private val day = RECHECK_LUCID_SECS + 1
    private val week = RECHECK_OTHER_SECS + 1

    private fun cached(
        provider: String,
        syncType: String = "RICH_SYNCED",
        pinned: Boolean = false,
    ) = LyricsEntity("aaaaaaaaaaa", error = false, lines = null, syncType = syncType, provider = provider, fetchedAt = 1, pinned = pinned)

    private fun due(
        l: LyricsEntity,
        age: Long,
        setting: String? = null,
    ) = LyricsRecheck.due(l, age, LyricsSource.order(setting))

    @Test
    fun lucidsOwnByRevisionAfterADay() {
        assertNull(due(cached("lucid"), RECHECK_LUCID_SECS))
        assertEquals(LyricsRecheck.REVISION, due(cached("lucid"), day))
    }

    @Test
    fun anotherSourceAsksAfterAWeekWhereLucidWouldWin() {
        assertNull(due(cached("boidu"), day))
        assertEquals(LyricsRecheck.ASK, due(cached("boidu"), week))
        // Synced from a source above Lucid: the walk would stop there again.
        assertNull(due(cached("lrclib", "LINE_SYNCED"), week, "lrclib,lucid"))
        // Plain text from that same source: Lucid's synced answer would win the walk.
        assertEquals(LyricsRecheck.ASK, due(cached("lrclib", "UNSYNCED"), week, "lrclib,lucid"))
        // A source switched off since: whatever a walk finds now, it isn't that.
        assertEquals(LyricsRecheck.ASK, due(cached("lrclib", "LINE_SYNCED"), week, "-lrclib,lucid"))
    }

    @Test
    fun neverPinnedNeverWithLucidOff() {
        assertNull(due(cached("lucid", pinned = true), week))
        assertNull(due(cached("boidu"), week, "-lucid"))
        assertNull(due(cached("lucid").copy(error = true), week))
    }
}
