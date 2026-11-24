package com.simohypers.lucid.domain.data.model.searchResult

import com.simohypers.lucid.domain.data.type.SearchResultType

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<SearchResultType>,
)