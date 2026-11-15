package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.model.scoreboard.EspnApiResponse
import com.zeyadmamoun.network.model.scoreboard.LeagueSlugDto
import com.zeyadmamoun.network.model.standings.EspnStandingsResponseDto
import com.zeyadmamoun.network.model.summary.EspnMatchSummaryResponseDto

interface NetworkDataSource {
    suspend fun getLeagues(): List<LeagueSlugDto>
    suspend fun getScoreboards(leagueSlug: String, date: String): EspnApiResponse
    suspend fun getStandings(leagueSlug: String): EspnStandingsResponseDto
    suspend fun getMatchSummary(leagueSlug: String, eventId: String): EspnMatchSummaryResponseDto
}