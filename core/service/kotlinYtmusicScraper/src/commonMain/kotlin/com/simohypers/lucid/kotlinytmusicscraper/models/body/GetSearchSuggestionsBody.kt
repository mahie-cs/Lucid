package com.simohypers.lucid.kotlinytmusicscraper.models.body

import com.simohypers.lucid.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)