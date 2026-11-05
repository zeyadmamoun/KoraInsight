package com.zeyadmamoun.network.model.scoreboard

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueSlugDto(
    @SerialName("id")
    val id: Long,
    @SerialName("league_uid")
    val leagueUid: String? = null,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("name")
    val name: String,
    @SerialName("logo_url")
    val logoUrl: String? = null,
    @SerialName("dark_logo_url")
    val darkLogoUrl: String? = null,
    @SerialName("has_standings")
    val hasStandings: Boolean = false,
)
