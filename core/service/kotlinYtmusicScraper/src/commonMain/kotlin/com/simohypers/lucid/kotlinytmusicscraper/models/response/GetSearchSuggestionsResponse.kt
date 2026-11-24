package com.simohypers.lucid.kotlinytmusicscraper.models.response

import com.simohypers.lucid.kotlinytmusicscraper.models.SearchSuggestionsSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsResponse(
    val contents: List<Content>?,
) {
    @Serializable
    data class Content(
        val searchSuggestionsSectionRenderer: SearchSuggestionsSectionRenderer,
    )
}