package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.model.EspnApiResponse
import com.zeyadmamoun.network.model.LeagueSlugDto

interface NetworkDataSource {
    suspend fun getLeagues(): List<LeagueSlugDto>
    suspend fun getScoreboards(leagueSlug: String, date: String): EspnApiResponse
}