package com.zeyadmamoun.data.model

import com.zeyadmamoun.network.model.LeagueSlugDto

data class LeagueSlug(
    val id: Long,
    val slug: String,
    val name: String,
)

fun LeagueSlugDto.toLeagueSlug(): LeagueSlug = LeagueSlug(
    id = id,
    slug = slug,
    name = name,
)
