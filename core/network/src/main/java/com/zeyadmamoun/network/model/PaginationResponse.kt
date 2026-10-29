package com.zeyadmamoun.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse(
    val totalCount: Int,
    val offset: Int,
    val limit: Int,
)
