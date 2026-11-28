package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.model.news.EspnArticleDetailsResponseDto
import com.zeyadmamoun.network.model.news.EspnNewsResponseDto
import com.zeyadmamoun.network.model.scoreboard.EspnApiResponse
import com.zeyadmamoun.network.model.scoreboard.LeagueSlugDto
import com.zeyadmamoun.network.model.standings.EspnStandingsResponseDto
import com.zeyadmamoun.network.model.summary.EspnMatchSummaryResponseDto

interface NetworkDataSource {
    suspend fun getLeagues(): List<LeagueSlugDto>
    suspend fun getScoreboards(leagueSlug: String, date: String): EspnApiResponse
    suspend fun getStandings(leagueSlug: String): EspnStandingsResponseDto
    suspend fun getMatchSummary(leagueSlug: String, eventId: String): EspnMatchSummaryResponseDto

    /**
     * Fetches the news feed of a single league.
     *
     * @param leagueSlug The ESPN league slug (e.g. `eng.1`, `uefa.champions`).
     * @param limit Maximum number of articles to return. ESPN caps this at 50.
     */
    suspend fun getNews(leagueSlug: String, limit: Int): EspnNewsResponseDto

    /**
     * Fetches full details for a single news article or media clip.
     *
     * @param articleId Unique ESPN article ID.
     */
    suspend fun getArticleDetails(articleId: String): EspnArticleDetailsResponseDto
}