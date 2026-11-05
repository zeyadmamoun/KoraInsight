package com.zeyadmamoun.data.model.scoreboard

import com.zeyadmamoun.core.common.datetime.calendarDateToCompactString
import com.zeyadmamoun.network.model.scoreboard.EspnLeagueDto
import com.zeyadmamoun.network.model.scoreboard.calendarDates

data class League(
    val id: String,
    val name: String,
    val abbreviation: String? = null,
    val slug: String,
    val season: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val calender: List<String> = emptyList(),
    val logos: List<LeagueLogo> = emptyList(),
    val matches: List<Match> = emptyList()
) {
    fun toRequestDates(): List<String> {
        return calender.map { it.calendarDateToCompactString() }
    }

    fun toRequestDate(date: String): String {
        return date.calendarDateToCompactString()
    }
}

data class LeagueLogo(
    val href: String,
    val width: Int? = null,
    val height: Int? = null,
    val alt: String = "",
    val rel: List<String> = emptyList(),
    val lastUpdated: String? = null
)

fun EspnLeagueDto.toLeague(): League = League(
    id = id,
    name = name,
    abbreviation = abbreviation,
    slug = slug,
    season = season?.year,
    startDate = season?.startDate,
    endDate = season?.endDate,
    calender = calendarDates,
    logos = logos.map { logoDto ->
        LeagueLogo(
            href = logoDto.href,
            width = logoDto.width,
            height = logoDto.height,
            alt = logoDto.alt ?: "",
            rel = logoDto.rel,
            lastUpdated = logoDto.lastUpdated
        )
    }
)
