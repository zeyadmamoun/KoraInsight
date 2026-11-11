package com.zeyadmamoun.matchdetails.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation 3 destination key for the Match Details screen.
 *
 * @property matchId The unique match ID (e.g. from ESPN / scoreboard).
 * @property leagueSlug The league slug identifier (e.g. "eng.1", "esp.1").
 */
@Serializable
data class MatchDetailsRoute(
    val matchId: String = "",
    val leagueSlug: String = ""
) : NavKey
