package com.simohypers.lucid.domain.data.model.library

import com.simohypers.lucid.domain.data.model.browse.album.Track

/** One day bucket of the account's history, named as YouTube names it ("Today", "Last week"). */
data class HistoryDay(
    val title: String,
    val tracks: List<Track>,
)
