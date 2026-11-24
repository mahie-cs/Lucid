package com.simohypers.lucid.domain.data.model.mood.genre

import com.simohypers.lucid.domain.data.model.searchResult.songs.Artist

data class ItemsSong(
    val title: String,
    val artist: List<Artist>?,
    val videoId: String,
)