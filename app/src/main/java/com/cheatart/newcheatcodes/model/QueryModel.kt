package com.cheatart.newcheatcodes.model

data class QueryModel(
    val platformsId: String?,
    val ordering: String?,
    val genres: String?,
    val searchQuery: String?
)