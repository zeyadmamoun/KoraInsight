package com.zeyadmamoun.fixtures.util

import com.zeyadmamoun.data.model.Match
import com.zeyadmamoun.data.model.Status

/**
 * Returns formatted short text for match status badges (e.g. "LIVE", "FT", "HT", "PRE", "45'").
 */
fun Status?.toDisplayBadge(): String {
    val state = this?.state?.trim()?.lowercase() ?: return ""
    return when (state) {
        "in" -> {
            val clock = this.displayClock?.trim()
            val detail = this.detail?.trim()
            when {
                detail?.equals("HT", ignoreCase = true) == true -> "HT"
                !clock.isNullOrBlank() && clock.length <= 4 -> clock
                !detail.isNullOrBlank() && detail.length <= 4 -> detail
                else -> "LIVE"
            }
        }
        "post", "pos" -> "FT"
        "pre" -> "PRE"
        else -> state.uppercase()
    }
}

fun Match?.toDisplayBadge(): String = this?.status.toDisplayBadge()

/**
 * Returns true if the match is currently in progress.
 */
fun Status?.isLive(): Boolean {
    return this?.state?.trim()?.equals("in", ignoreCase = true) == true
}

fun Match?.isLive(): Boolean = this?.status.isLive()

/**
 * Returns formatted score text (e.g. "1 - 3") or "VS" if match has not started yet.
 */
fun Match.toScoreText(): String {
    val state = status.state.trim().lowercase()
    if (state == "pre") {
        return "VS"
    }

    val homeScore = homeTeam?.score
    val awayScore = awayTeam?.score

    return if (!homeScore.isNullOrBlank() && !awayScore.isNullOrBlank()) {
        "$homeScore - $awayScore"
    } else {
        "VS"
    }
}
