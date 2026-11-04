package com.zeyadmamoun.data.model

import com.zeyadmamoun.network.model.EspnApiResponse

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
