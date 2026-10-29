package com.zeyadmamoun.network.ktor

import com.zeyadmamoun.network.BuildConfig
import com.zeyadmamoun.network.model.LeagueDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorClient {
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

    suspend fun getLeagues(): List<LeagueDto>? {
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
}
