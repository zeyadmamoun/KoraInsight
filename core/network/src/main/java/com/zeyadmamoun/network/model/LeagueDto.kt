package com.zeyadmamoun.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("logo")
    val logo: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("country_name")
    val countryName: String? = null,
    @SerialName("country_logo")
    val countryLogo: String? = null,
    @SerialName("seasons")
    val seasons: List<SeasonDto> = emptyList(),
    @SerialName("last_synced_at")
    val lastSyncedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)
