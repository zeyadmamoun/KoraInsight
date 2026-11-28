package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.BuildConfig
import com.zeyadmamoun.network.model.news.EspnArticleDetailsResponseDto
import com.zeyadmamoun.network.model.news.EspnNewsResponseDto
import com.zeyadmamoun.network.model.scoreboard.EspnApiResponse
import com.zeyadmamoun.network.model.scoreboard.LeagueSlugDto
import com.zeyadmamoun.network.model.standings.EspnStandingsResponseDto
import com.zeyadmamoun.network.model.summary.EspnMatchSummaryResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KtorNetworkDataSource @Inject constructor(
    private val client: HttpClient
) : NetworkDataSource {
    override suspend fun getLeagues(): List<LeagueSlugDto> {
        return client.get("https://agljazbgodojwgjhnzfr.supabase.co/rest/v1/featured_leagues") {
            header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
        }.body()
    }

    override suspend fun getScoreboards(leagueSlug: String, date: String): EspnApiResponse {
        return client.get("https://site.api.espn.com/apis/site/v2/sports/soccer/${leagueSlug}/scoreboard") {
            url {
                if (date.isNotBlank()) {
                    parameters.append("dates", date)
                }
            }
        }.body()
    }

    override suspend fun getStandings(leagueSlug: String): EspnStandingsResponseDto {
        return client.get("https://site.api.espn.com/apis/v2/sports/soccer/$leagueSlug/standings").body()
    }

    override suspend fun getMatchSummary(leagueSlug: String, eventId: String): EspnMatchSummaryResponseDto {
        val slug = leagueSlug.ifBlank { "all" }
        return client.get("https://site.api.espn.com/apis/site/v2/sports/soccer/$slug/summary") {
            url {
                parameters.append("event", eventId)
            }
        }.body()
    }

    override suspend fun getNews(leagueSlug: String, limit: Int): EspnNewsResponseDto {
        return client.get("https://site.api.espn.com/apis/site/v2/sports/soccer/$leagueSlug/news") {
            url {
                parameters.append("limit", limit.toString())
            }
        }.body()
    }

    override suspend fun getArticleDetails(articleId: String): EspnArticleDetailsResponseDto {
        return client.get("https://content.core.api.espn.com/v1/sports/news/$articleId").body()
    }
}
