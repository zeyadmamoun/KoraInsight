package com.zeyadmamoun.data.model.scoreboard

import com.zeyadmamoun.network.model.scoreboard.EspnAddressDto
import com.zeyadmamoun.network.model.scoreboard.EspnCompetitorDto
import com.zeyadmamoun.network.model.scoreboard.EspnEventDto
import com.zeyadmamoun.network.model.scoreboard.EspnStatusDto
import com.zeyadmamoun.network.model.scoreboard.EspnTicketDto
import com.zeyadmamoun.network.model.scoreboard.EspnVenueDto

data class Match(
    val id: String,
    val date: String,
    val name: String,
    val shortName: String? = null,
    val status: Status,
    val venue: Venue? = null,
    val tickets: List<Ticket> = emptyList(),
    val teams: List<Team> = emptyList(),
    val roundTitle: String? = null,
    val leg: String? = null,
    val note: String? = null,
) {
    val homeTeam: Team? get() = teams.firstOrNull { it.homeAway == "home" }
    val awayTeam: Team? get() = teams.firstOrNull { it.homeAway == "away" }
}

data class Status(
    val clock: Int? = null,
    val displayClock: String? = null,
    val period: Int? = null,
    val state: String,
    val name: String,
    val completed: Boolean = false,
    val description: String,
    val detail: String? = null,   //detail is the minute they are currently playing
)

data class Venue(
    val id: String? = null,
    val fullName: String? = null,
    val address: Address? = null,
)

data class Address(
    val city: String? = null,
    val country: String? = null,
)

data class Ticket(
    val summary: String? = null,
    val numberAvailable: Int? = null,
    val links: List<String> = emptyList(),
)

data class Team(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val abbreviation: String? = null,
    val logo: String? = null,
    val color: String? = null,
    val alternateColor: String? = null,
    val homeAway: String? = null,
    val score: String? = null,
    val shootoutScore: Int? = null,
    val aggregateScore: Int? = null,
    val winner: Boolean? = null,
    val form: String? = null,
)

val EspnEventDto.venue: EspnVenueDto?
    get() = competitions.firstOrNull()?.venue

val EspnEventDto.tickets: List<Ticket>
    get() = competitions.firstOrNull()?.tickets.orEmpty().map { it.toTicket() }

val EspnEventDto.teams: List<Team>
    get() {
        val comp = competitions.firstOrNull() ?: return emptyList()
        val seriesCompMap = comp.series?.competitors.orEmpty().associateBy { it.id }
        return comp.competitors.map { competitor ->
            val seriesComp = seriesCompMap[competitor.team.id] ?: seriesCompMap[competitor.id]
            competitor.toTeam(
                aggregateScore = seriesComp?.aggregateScore?.toInt()
            )
        }
    }

fun EspnEventDto.toMatch(): Match {
    val comp = competitions.firstOrNull()
    return Match(
        id = id,
        date = date,
        name = name,
        shortName = shortName,
        status = status.toStatus(),
        venue = venue.toVenue(),
        tickets = tickets,
        teams = teams,
        roundTitle = comp?.series?.title,
        leg = comp?.leg?.displayValue,
        note = comp?.notes?.firstOrNull()?.headline ?: comp?.notes?.firstOrNull()?.text,
    )
}

fun EspnStatusDto?.toStatus(): Status = Status(
    clock = this?.clock?.toInt(),
    displayClock = this?.displayClock,
    period = this?.period,
    state = this?.type?.state.orEmpty(),
    name = this?.type?.name.orEmpty(),
    completed = this?.type?.completed ?: false,
    description = this?.type?.description.orEmpty(),
    detail = this?.type?.detail ?: this?.type?.shortDetail,
)

fun EspnVenueDto?.toVenue(): Venue? {
    if (this == null) return null
    return Venue(
        id = id,
        fullName = fullName,
        address = address?.toAddress(),
    )
}

fun EspnAddressDto.toAddress(): Address = Address(
    city = city,
    country = country,
)

fun EspnTicketDto.toTicket(): Ticket = Ticket(
    summary = summary,
    numberAvailable = numberAvailable,
    links = links.mapNotNull { it.href },
)

fun EspnCompetitorDto.toTeam(aggregateScore: Int? = null): Team = Team(
    id = team.id,
    name = team.name ?: team.displayName,
    displayName = team.displayName,
    abbreviation = team.abbreviation,
    logo = team.logo,
    color = team.color,
    alternateColor = team.alternateColor,
    homeAway = homeAway,
    score = score,
    shootoutScore = shootoutScore,
    aggregateScore = aggregateScore,
    winner = winner,
    form = form,
)
