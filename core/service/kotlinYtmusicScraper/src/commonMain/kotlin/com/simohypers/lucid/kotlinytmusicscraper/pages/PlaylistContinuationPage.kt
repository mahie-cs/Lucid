package com.simohypers.lucid.kotlinytmusicscraper.pages

import com.simohypers.lucid.kotlinytmusicscraper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)