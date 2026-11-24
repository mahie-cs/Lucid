package com.simohypers.lucid.domain.data.model.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals

class LyricsSourceTest {
    private fun ids(setting: String?) = LyricsSource.encode(LyricsSource.order(setting))

    @Test
    fun orderDropsUnknownIdsAndAddsTheRest() {
        assertEquals("lucid,boidu,unison,lyricsplus,lrclib,youtube,netease,qq,kugou", ids(null))
        // SimpMusic's id, from an order saved before it went, is dropped like any unknown one.
        // Lucid, not named yet, goes first; the rest that aren't named go last.
        assertEquals("lucid,lrclib,-boidu,unison,lyricsplus,youtube,netease,qq,kugou", ids("lrclib, -boidu,simpmusic,lrclib"))
        // Named, it stays where the user put it, on or off.
        assertEquals("lrclib,lucid,boidu,unison,lyricsplus,youtube,netease,qq,kugou", ids("lrclib,lucid,boidu"))
        assertEquals("boidu,-lucid,unison,lyricsplus,lrclib,youtube,netease,qq,kugou", ids("boidu,-lucid"))
    }

    @Test
    fun everySourceOffStaysOffWhenAReleaseAddsOne() {
        assertEquals(
            "-lucid,-boidu,-lyricsplus,-lrclib,-youtube,-netease,-qq,-kugou,-unison",
            ids("-boidu,-lyricsplus,-lrclib,-youtube,-simpmusic,-netease,-qq,-kugou"),
        )
    }
}
