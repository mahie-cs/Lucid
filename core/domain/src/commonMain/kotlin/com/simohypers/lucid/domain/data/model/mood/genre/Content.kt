package com.simohypers.lucid.domain.data.model.mood.genre

import com.simohypers.lucid.domain.data.model.searchResult.songs.Thumbnail
import com.simohypers.lucid.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType