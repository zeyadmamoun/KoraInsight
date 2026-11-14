package com.zeyadmamoun.network.model.summary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Root response DTO for ESPN's Match Summary endpoint (/summary?event={id}).
 */
@Serializable
data class EspnMatchSummaryResponseDto(
    @SerialName("header")
    val header: EspnSummaryHeaderDto? = null,
    @SerialName("boxscore")
    val boxscore: EspnSummaryBoxscoreDto? = null,
    @SerialName("gameInfo")
    val gameInfo: EspnSummaryGameInfoDto? = null,
    @SerialName("keyEvents")
    val keyEvents: List<EspnSummaryKeyEventDto> = emptyList(),
    @SerialName("rosters")
    val rosters: List<EspnSummaryRosterDto> = emptyList(),
)

@Serializable
data class EspnSummaryHeaderDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("league")
    val league: EspnSummaryLeagueDto? = null,
    @SerialName("season")
    val season: EspnSummarySeasonDto? = null,
    @SerialName("competitions")
    val competitions: List<EspnSummaryCompetitionDto> = emptyList(),
)

@Serializable
data class EspnSummaryLeagueDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("slug")
    val slug: String? = null,
)

@Serializable
data class EspnSummarySeasonDto(
    @SerialName("year")
    val year: Int? = null,
    @SerialName("name")
    val name: String? = null,
)

@Serializable
data class EspnSummaryCompetitionDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("status")
    val status: EspnSummaryStatusDto? = null,
    @SerialName("competitors")
    val competitors: List<EspnSummaryCompetitorDto> = emptyList(),
)

@Serializable
data class EspnSummaryCompetitorDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("homeAway")
    val homeAway: String? = null,
    @SerialName("winner")
    val winner: Boolean? = null,
    @SerialName("score")
    val score: String? = null,
    @SerialName("team")
    val team: EspnSummaryTeamHeaderDto? = null,
)

@Serializable
data class EspnSummaryTeamHeaderDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("logos")
    val logos: List<EspnSummaryLogoDto> = emptyList(),
)

@Serializable
data class EspnSummaryLogoDto(
    @SerialName("href")
    val href: String? = null,
)

@Serializable
data class EspnSummaryStatusDto(
    @SerialName("clock")
    val clock: Double? = null,
    @SerialName("displayClock")
    val displayClock: String? = null,
    @SerialName("period")
    val period: Int? = null,
    @SerialName("type")
    val type: EspnSummaryStatusTypeDto? = null,
)

@Serializable
data class EspnSummaryStatusTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("completed")
    val completed: Boolean = false,
    @SerialName("description")
    val description: String? = null,
    @SerialName("detail")
    val detail: String? = null,
    @SerialName("shortDetail")
    val shortDetail: String? = null,
)

@Serializable
data class EspnSummaryBoxscoreDto(
    @SerialName("teams")
    val teams: List<EspnSummaryBoxscoreTeamDto> = emptyList(),
)

@Serializable
data class EspnSummaryBoxscoreTeamDto(
    @SerialName("team")
    val team: EspnSummaryBoxscoreTeamDetailsDto? = null,
    @SerialName("statistics")
    val statistics: List<EspnSummaryStatDto> = emptyList(),
    @SerialName("homeAway")
    val homeAway: String? = null,
)

@Serializable
data class EspnSummaryBoxscoreTeamDetailsDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("logo")
    val logo: String? = null,
    @SerialName("color")
    val color: String? = null,
    @SerialName("alternateColor")
    val alternateColor: String? = null,
)

@Serializable
data class EspnSummaryStatDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayValue")
    val displayValue: String? = null,
    @SerialName("label")
    val label: String? = null,
)

@Serializable
data class EspnSummaryGameInfoDto(
    @SerialName("venue")
    val venue: EspnSummaryVenueDto? = null,
    @SerialName("attendance")
    val attendance: Int? = null,
)

@Serializable
data class EspnSummaryVenueDto(
    @SerialName("fullName")
    val fullName: String? = null,
    @SerialName("address")
    val address: EspnSummaryAddressDto? = null,
)

@Serializable
data class EspnSummaryAddressDto(
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
)

@Serializable
data class EspnSummaryKeyEventDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("type")
    val type: EspnSummaryEventTypeDto? = null,
    @SerialName("clock")
    val clock: EspnSummaryClockDto? = null,
    @SerialName("scoringPlay")
    val scoringPlay: Boolean = false,
    @SerialName("text")
    val text: String? = null,
    @SerialName("shortText")
    val shortText: String? = null,
    @SerialName("team")
    val team: EspnSummaryTeamRefDto? = null,
    @SerialName("participants")
    val participants: List<EspnSummaryParticipantDto> = emptyList(),
)

@Serializable
data class EspnSummaryEventTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("type")
    val type: String? = null,
)

@Serializable
data class EspnSummaryClockDto(
    @SerialName("value")
    val value: Double? = null,
    @SerialName("displayValue")
    val displayValue: String? = null,
)

@Serializable
data class EspnSummaryTeamRefDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
)

@Serializable
data class EspnSummaryParticipantDto(
    @SerialName("athlete")
    val athlete: EspnSummaryAthleteDto? = null,
)

@Serializable
data class EspnSummaryRosterDto(
    @SerialName("team")
    val team: EspnSummaryTeamRefDto? = null,
    @SerialName("formation")
    val formation: String? = null,
    @SerialName("roster")
    val roster: List<EspnSummaryPlayerDto> = emptyList(),
)

@Serializable
data class EspnSummaryPlayerDto(
    @SerialName("starter")
    val starter: Boolean = false,
    @SerialName("subbedIn")
    val subbedIn: Boolean = false,
    @SerialName("subbedOut")
    val subbedOut: Boolean = false,
    @SerialName("jersey")
    val jersey: String? = null,
    @SerialName("athlete")
    val athlete: EspnSummaryAthleteDto? = null,
    @SerialName("position")
    val position: EspnSummaryPositionDto? = null,
)

@Serializable
data class EspnSummaryAthleteDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("jersey")
    val jersey: String? = null,
    @SerialName("position")
    val position: EspnSummaryPositionDto? = null,
)

@Serializable
data class EspnSummaryPositionDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
)
