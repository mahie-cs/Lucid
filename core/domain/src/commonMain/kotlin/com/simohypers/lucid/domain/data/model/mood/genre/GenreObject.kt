package com.simohypers.lucid.domain.data.model.mood.genre

data class GenreObject(
    val header: String,
    val itemsPlaylist: List<ItemsPlaylist>,
    val itemsSong: List<ItemsSong>?,
)