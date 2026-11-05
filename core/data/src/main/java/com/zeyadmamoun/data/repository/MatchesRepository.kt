package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.scoreboard.Scoreboard
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for football leagues and match fixture data.
 *
 * Coordinates data retrieval from remote network APIs (and future local cache/database),
 * exposing clean domain models wrapped in [Result] streams for the UI layer.
 */
interface MatchesRepository {

    /**
     * Retrieves match scoreboards for multiple leagues on a specific date.
     *
     * Executes requests in parallel across all provided [leagueSlugs]. Employs partial
     * failure resilience: if one league fails, remaining successful leagues are retained.
     *
     * @param leagueSlugs The list of [LeagueSlug] targets to query.
     * @param date The match date in compact ISO format (`yyyyMMdd`, e.g., `"20261103"`).
     * @return A cold [Flow] emitting:
     * - [Result.Loading] while requests are in flight.
     * - [Result.Success] containing the aggregated list of [Scoreboard]s.
     * - [Result.Error] if the slug list is empty or all league requests fail.
     */
    fun getMatchesForLeague(
        leagueSlugs: List<LeagueSlug>,
        date: String,
        isForceRefresh: Boolean
    ): Flow<Result<List<Scoreboard>>>
}
