package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.BuildConfig
import com.zeyadmamoun.network.model.EspnApiResponse
import com.zeyadmamoun.network.model.LeagueSlugDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    var client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    suspend fun getLeagues(): List<LeagueSlugDto>? {
        return try {
            client.get("https://agljazbgodojwgjhnzfr.supabase.co/rest/v1/featured_leagues") {
                header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getScoreboards(leagueSlug: String, date: String): EspnApiResponse? {
        return try {
            client.get("https://site.api.espn.com/apis/site/v2/sports/soccer/${leagueSlug}/scoreboard"){
                url {
                    parameters.append("dates", date)
                }
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
