package com.zeyadmamoun.data.model.scoreboard

import com.zeyadmamoun.network.model.scoreboard.LeagueSlugDto

data class LeagueSlug(
    val id: Long,
    val leagueUid: String? = null,
    val slug: String,
    val name: String,
    val logoUrl: String? = null,
    val darkLogoUrl: String? = null,
    val hasStandings: Boolean = false,
)

fun LeagueSlugDto.toLeagueSlug(): LeagueSlug = LeagueSlug(
    id = id,
    leagueUid = leagueUid,
    slug = slug,
    name = name,
    logoUrl = logoUrl,
    darkLogoUrl = darkLogoUrl,
    hasStandings = hasStandings,
)
