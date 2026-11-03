package com.zeyadmamoun.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class EspnApiResponse(
    @SerialName("leagues")
    val leagues: List<EspnLeagueDto> = emptyList(),
    @SerialName("season")
    val season: EspnSeasonHeaderDto? = null,
    @SerialName("day")
    val day: EspnDayDto? = null,
    @SerialName("events")
    val events: List<EspnEventDto> = emptyList(),
)

typealias EspnScoreboardResponse = EspnApiResponse
typealias EspnScoreboardResponseDto = EspnApiResponse

@Serializable
data class EspnDayDto(
    @SerialName("date")
    val date: String? = null,
)

@Serializable
data class EspnSeasonHeaderDto(
    @SerialName("type")
    val type: Int? = null,
    @SerialName("year")
    val year: Int? = null,
)

@Serializable
data class EspnLeagueDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("midsizeName")
    val midsizeName: String? = null,
    @SerialName("slug")
    val slug: String,
    @SerialName("season")
    val season: EspnLeagueSeasonDto? = null,
    @SerialName("logos")
    val logos: List<EspnLogoDto> = emptyList(),
    @SerialName("calendarType")
    val calendarType: String? = null,
    @SerialName("calendarIsWhitelist")
    val calendarIsWhitelist: Boolean? = null,
    @SerialName("calendarStartDate")
    val calendarStartDate: String? = null,
    @SerialName("calendarEndDate")
    val calendarEndDate: String? = null,
    @SerialName("calendar")
    val calendar: List<JsonElement> = emptyList(),
)

val EspnLeagueDto.calendarDates: List<String>
    get() = calendar.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }

@Serializable
data class EspnLeagueSeasonDto(
    @SerialName("year")
    val year: Int? = null,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("endDate")
    val endDate: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("type")
    val type: EspnSeasonTypeDto? = null,
)

@Serializable
data class EspnSeasonTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("type")
    val type: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
)

@Serializable
data class EspnLogoDto(
    @SerialName("href")
    val href: String,
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
data class EspnEventDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("date")
    val date: String,
    @SerialName("name")
    val name: String,
    @SerialName("shortName")
    val shortName: String? = null,
    @SerialName("season")
    val season: EspnEventSeasonDto? = null,
    @SerialName("competitions")
    val competitions: List<EspnCompetitionDto> = emptyList(),
    @SerialName("status")
    val status: EspnStatusDto? = null,
)

@Serializable
data class EspnEventSeasonDto(
    @SerialName("year")
    val year: Int? = null,
    @SerialName("type")
    val type: Int? = null,
    @SerialName("slug")
    val slug: String? = null,
)

@Serializable
data class EspnCompetitionDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("date")
    val date: String,
    @SerialName("startDate")
    val startDate: String? = null,
    @SerialName("attendance")
    val attendance: Int? = null,
    @SerialName("timeValid")
    val timeValid: Boolean? = null,
    @SerialName("recent")
    val recent: Boolean? = null,
    @SerialName("status")
    val status: EspnStatusDto? = null,
    @SerialName("venue")
    val venue: EspnVenueDto? = null,
    @SerialName("competitors")
    val competitors: List<EspnCompetitorDto> = emptyList(),
    @SerialName("details")
    val details: List<EspnDetailDto> = emptyList(),
    @SerialName("format")
    val format: EspnFormatDto? = null,
    @SerialName("notes")
    val notes: List<EspnNoteDto> = emptyList(),
    @SerialName("geoBroadcasts")
    val geoBroadcasts: List<EspnGeoBroadcastDto> = emptyList(),
    @SerialName("broadcasts")
    val broadcasts: List<EspnBroadcastDto> = emptyList(),
    @SerialName("tickets")
    val tickets: List<EspnTicketDto> = emptyList(),
)

@Serializable
data class EspnTicketDto(
    @SerialName("summary")
    val summary: String? = null,
    @SerialName("numberAvailable")
    val numberAvailable: Int? = null,
    @SerialName("links")
    val links: List<EspnLinkDto> = emptyList(),
)

@Serializable
data class EspnStatusDto(
    @SerialName("clock")
    val clock: Double? = null,
    @SerialName("displayClock")
    val displayClock: String? = null,
    @SerialName("period")
    val period: Int? = null,
    @SerialName("type")
    val type: EspnStatusTypeDto? = null,
)

@Serializable
data class EspnStatusTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: String,
    @SerialName("completed")
    val completed: Boolean = false,
    @SerialName("description")
    val description: String,
    @SerialName("detail")
    val detail: String? = null,
    @SerialName("shortDetail")
    val shortDetail: String? = null,
)

@Serializable
data class EspnVenueDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("fullName")
    val fullName: String? = null,
    @SerialName("address")
    val address: EspnAddressDto? = null,
)

@Serializable
data class EspnAddressDto(
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
)

@Serializable
data class EspnCompetitorDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("order")
    val order: Int? = null,
    @SerialName("homeAway")
    val homeAway: String,
    @SerialName("winner")
    val winner: Boolean? = null,
    @SerialName("form")
    val form: String? = null,
    @SerialName("score")
    val score: String? = null,
    @SerialName("records")
    val records: List<EspnRecordDto> = emptyList(),
    @SerialName("team")
    val team: EspnTeamDto,
    @SerialName("statistics")
    val statistics: List<EspnStatisticDto> = emptyList(),
)

@Serializable
data class EspnTeamDto(
    @SerialName("id")
    val id: String,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("shortDisplayName")
    val shortDisplayName: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("location")
    val location: String? = null,
    @SerialName("color")
    val color: String? = null,
    @SerialName("alternateColor")
    val alternateColor: String? = null,
    @SerialName("isActive")
    val isActive: Boolean? = null,
    @SerialName("logo")
    val logo: String? = null,
    @SerialName("links")
    val links: List<EspnLinkDto> = emptyList(),
)

@Serializable
data class EspnRecordDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("summary")
    val summary: String? = null,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
)

@Serializable
data class EspnStatisticDto(
    @SerialName("name")
    val name: String,
    @SerialName("abbreviation")
    val abbreviation: String? = null,
    @SerialName("displayValue")
    val displayValue: String,
)

@Serializable
data class EspnDetailDto(
    @SerialName("type")
    val type: EspnDetailTypeDto? = null,
    @SerialName("clock")
    val clock: EspnClockDto? = null,
    @SerialName("team")
    val team: EspnTeamRefDto? = null,
    @SerialName("scoreValue")
    val scoreValue: Int? = null,
    @SerialName("scoringPlay")
    val scoringPlay: Boolean = false,
    @SerialName("redCard")
    val redCard: Boolean = false,
    @SerialName("yellowCard")
    val yellowCard: Boolean = false,
    @SerialName("penaltyKick")
    val penaltyKick: Boolean = false,
    @SerialName("ownGoal")
    val ownGoal: Boolean = false,
    @SerialName("shootout")
    val shootout: Boolean = false,
    @SerialName("athletesInvolved")
    val athletesInvolved: List<EspnAthleteDto> = emptyList(),
)

@Serializable
data class EspnDetailTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("text")
    val text: String? = null,
)

@Serializable
data class EspnClockDto(
    @SerialName("value")
    val value: Double? = null,
    @SerialName("displayValue")
    val displayValue: String? = null,
)

@Serializable
data class EspnTeamRefDto(
    @SerialName("id")
    val id: String? = null,
)

@Serializable
data class EspnAthleteDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("displayName")
    val displayName: String? = null,
    @SerialName("shortName")
    val shortName: String? = null,
    @SerialName("fullName")
    val fullName: String? = null,
    @SerialName("jersey")
    val jersey: String? = null,
    @SerialName("position")
    val position: String? = null,
    @SerialName("team")
    val team: EspnTeamRefDto? = null,
)

@Serializable
data class EspnFormatDto(
    @SerialName("regulation")
    val regulation: EspnRegulationDto? = null,
)

@Serializable
data class EspnRegulationDto(
    @SerialName("periods")
    val periods: Int? = null,
)

@Serializable
data class EspnNoteDto(
    @SerialName("type")
    val type: String? = null,
    @SerialName("headline")
    val headline: String? = null,
)

@Serializable
data class EspnGeoBroadcastDto(
    @SerialName("type")
    val type: EspnBroadcastTypeDto? = null,
    @SerialName("market")
    val market: EspnBroadcastMarketDto? = null,
    @SerialName("media")
    val media: EspnMediaDto? = null,
    @SerialName("lang")
    val lang: String? = null,
    @SerialName("region")
    val region: String? = null,
)

@Serializable
data class EspnBroadcastTypeDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("shortName")
    val shortName: String? = null,
)

@Serializable
data class EspnBroadcastMarketDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("type")
    val type: String? = null,
)

@Serializable
data class EspnMediaDto(
    @SerialName("shortName")
    val shortName: String? = null,
    @SerialName("logo")
    val logo: String? = null,
    @SerialName("darkLogo")
    val darkLogo: String? = null,
)

@Serializable
data class EspnBroadcastDto(
    @SerialName("market")
    val market: String? = null,
    @SerialName("names")
    val names: List<String> = emptyList(),
)

@Serializable
data class EspnLinkDto(
    @SerialName("rel")
    val rel: List<String> = emptyList(),
    @SerialName("href")
    val href: String? = null,
    @SerialName("text")
    val text: String? = null,
)
