package com.zeyadmamoun.network.model

import kotlinx.serialization.Serializable

@Serializable
data class HighlightlyResponse<T>(
    val data: T,
    val pagination: PaginationResponse,
    val plan: PlanResponse
)
