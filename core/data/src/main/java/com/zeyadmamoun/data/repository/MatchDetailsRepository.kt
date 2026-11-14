package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.summary.MatchDetails
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for detailed match summary, statistics, lineups, and commentary.
 */
interface MatchDetailsRepository {

    /**
     * Retrieves detailed match data for a given [matchId].
     *
     * @param matchId The ESPN event ID.
     * @param leagueSlug The optional league slug (e.g. "uefa.nations", "eng.1") or empty for fallback.
     * @param isForceRefresh Whether to bypass local cache and force fresh network fetch.
     * @return A cold [Flow] emitting [Result] with the mapped [MatchDetails].
     */
    fun getMatchDetails(
        matchId: String,
        leagueSlug: String = "",
        isForceRefresh: Boolean = false
    ): Flow<Result<MatchDetails>>
}
