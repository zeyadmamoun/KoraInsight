package com.zeyadmamoun.fixtures.util

import com.zeyadmamoun.network.model.EspnEventDto
import com.zeyadmamoun.network.model.EspnStatusDto

/**
 * Returns formatted short text for match status badges (e.g. "LIVE", "FT", "HT", "PRE", "45'").
 */
fun EspnStatusDto?.toDisplayBadge(): String {
    val state = this?.type?.state?.trim()?.lowercase() ?: return ""
    return when (state) {
        "in" -> {
            val clock = this.displayClock?.trim()
            val shortDetail = this.type?.shortDetail?.trim()
            when {
                shortDetail?.equals("HT", ignoreCase = true) == true -> "HT"
                !clock.isNullOrBlank() && clock.length <= 4 -> clock
                !shortDetail.isNullOrBlank() && shortDetail.length <= 4 -> shortDetail
                else -> "LIVE"
            }
        }
        "post", "pos" -> "FT"
        "pre" -> "PRE"
        else -> state.uppercase()
    }
}

/**
 * Returns true if the match is currently in progress.
 */
fun EspnStatusDto?.isLive(): Boolean {
    return this?.type?.state?.trim()?.equals("in", ignoreCase = true) == true
}

/**
 * Returns formatted score text (e.g. "1 - 3") or "VS" if match has not started yet.
 */
fun EspnEventDto.toScoreText(): String {
    val state = status?.type?.state?.trim()?.lowercase()
    if (state == "pre") {
        return "VS"
    }

    val competition = competitions.firstOrNull()
    val homeScore = competition?.competitors?.find { it.homeAway.equals("home", ignoreCase = true) }?.score
        ?: competition?.competitors?.getOrNull(0)?.score
    val awayScore = competition?.competitors?.find { it.homeAway.equals("away", ignoreCase = true) }?.score
        ?: competition?.competitors?.getOrNull(1)?.score

    return if (!homeScore.isNullOrBlank() && !awayScore.isNullOrBlank()) {
        "$homeScore - $awayScore"
    } else {
        "VS"
    }
}
