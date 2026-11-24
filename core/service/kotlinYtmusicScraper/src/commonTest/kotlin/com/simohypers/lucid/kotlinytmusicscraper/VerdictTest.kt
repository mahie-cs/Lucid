package com.simohypers.lucid.kotlinytmusicscraper

import com.simohypers.lucid.kotlinytmusicscraper.models.response.PlayerResponse.PlayabilityStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VerdictTest {
    private val gone = PlayabilityStatus("UNPLAYABLE", "Video unavailable")
    private val bot = PlayabilityStatus("UNPLAYABLE", "Sign in to confirm you’re not a bot")
    private val ok = PlayabilityStatus("OK", null)

    @Test
    fun mainRefusalBeatsTheFallbacksBotCheck() {
        // Issue #16: WEB_REMIX said unavailable, VISIONOS and TVHTML5_SIMPLY said bot.
        assertEquals(gone, verdict(gone, listOf(gone, bot, bot), streamRefused = false))
    }

    @Test
    fun botCheckOutranksWhenMainAnsweredOkOrNotAtAll() {
        assertEquals(bot, verdict(ok, listOf(gone, bot), streamRefused = true))
        assertEquals(bot, verdict(null, listOf(gone, bot), streamRefused = false))
        assertEquals(bot, verdict(bot, listOf(bot, gone), streamRefused = false))
    }

    @Test
    fun aStreamThatFailedIsNotUnavailable() {
        assertNull(verdict(ok, listOf(gone), streamRefused = true))
        assertEquals(gone, verdict(null, listOf(gone), streamRefused = false))
    }
}
