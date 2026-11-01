package com.zeyadmamoun.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueSlugDto(
    @SerialName("id")
    val id: Long,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("name")
    val name: String,
)
