package com.simohypers.lucid.kotlinytmusicscraper.models.body

import com.simohypers.lucid.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)