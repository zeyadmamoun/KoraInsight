package com.zeyadmamoun.data.model.scoreboard

import com.zeyadmamoun.network.model.scoreboard.EspnApiResponse

data class Scoreboard(
    val leagues: List<League> = emptyList(),
    val day: String? = null,
    val matches: List<Match> = emptyList()
)

fun EspnApiResponse.toScoreboard(): Scoreboard {
    return Scoreboard(
        leagues = leagues.map { it.toLeague() },
        day = day?.date,
        matches = events.map { it.toMatch() }
    )
}
