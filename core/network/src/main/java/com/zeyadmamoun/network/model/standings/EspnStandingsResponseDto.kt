package com.zeyadmamoun.network.model.standings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnStandingsResponseDto(
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("children")
    val children: List<StandingsChildDto> = emptyList(),
    @SerialName("season")
    val season: StandingsSeasonDto? = null,
)

@Serializable
data class StandingsSeasonDto(
    @SerialName("year")
    val year: Int? = null,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("endDate")
    val endDate: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
)

typealias EspnStandingsResponse = EspnStandingsResponseDto

@Serializable
data class StandingsChildDto(
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("standings")
    val standings: StandingsDto? = null,
)

@Serializable
data class StandingsDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("links")
    val links: List<StandingsLinkDto> = emptyList(),
    @SerialName("season")
    val season: Int? = null,
    @SerialName("seasonType")
    val seasonType: Int? = null,
    @SerialName("seasonDisplayName")
    val seasonDisplayName: String? = null,
    @SerialName("entries")
    val entries: List<StandingsEntryDto> = emptyList(),
)

@Serializable
data class StandingsLinkDto(
    @SerialName("language")
    val language: String? = null,
    @SerialName("rel")
    val rel: List<String> = emptyList(),
    @SerialName("href")
    val href: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("shortText")
    val shortText: String? = null,
    @SerialName("isExternal")
    val isExternal: Boolean? = null,
    @SerialName("isPremium")
    val isPremium: Boolean? = null,
)

@Serializable
data class StandingsEntryDto(
    @SerialName("team")
    val team: StandingsTeamDto,
    @SerialName("note")
    val note: StandingsNoteDto? = null,
    @SerialName("stats")
    val stats: List<StandingsStatDto> = emptyList(),
)

@Serializable
data class StandingsTeamDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("shortDisplayName")
    val shortDisplayName: String? = null,
    @SerialName("isActive")
    val isActive: Boolean? = null,
    @SerialName("isNational")
    val isNational: Boolean? = null,
    @SerialName("logos")
    val logos: List<StandingsLogoDto> = emptyList(),
)

@Serializable
data class StandingsLogoDto(
    @SerialName("href")
    val href: String? = null,
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("alt")
    val alt: String? = null,
    @SerialName("rel")
    val rel: List<String> = emptyList(),
    @SerialName("lastUpdated")
    val lastUpdated: String? = null,
)

@Serializable
data class StandingsNoteDto(
    @SerialName("color")
    val color: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("rank")
    val rank: Int? = null,
)

@Serializable
data class StandingsStatDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("shortDisplayName")
    val shortDisplayName: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("value")
    val value: Double? = null,
    @SerialName("displayValue")
    val displayValue: String? = null,
)
