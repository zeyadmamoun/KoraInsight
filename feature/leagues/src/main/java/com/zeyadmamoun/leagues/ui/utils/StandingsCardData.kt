package com.zeyadmamoun.leagues.ui.utils

import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings

data class StandingsCardData(
    val cardLeagues: List<LeagueSlug>,
    val standings: List<LeagueStandings>
)

fun List<LeagueSlug>.toCardLeagues(): List<LeagueSlug> = this.filter { it.hasStandings }

fun List<LeagueStandings>.toCardStandings(cardLeagues: List<LeagueSlug>): List<LeagueStandings>{
    val uids = cardLeagues.mapNotNull { it.leagueUid }.toSet()
    return this.filter { uids.contains(it.leagueUid) }
}
