package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings
import kotlinx.coroutines.flow.Flow

interface LeaguesRepository {

    /**
     * Retrieves the list of featured league slugs supported by the application.
     *
     * @return A cold [Flow] emitting:
     * - [Result.Loading] when the fetch begins.
     * - [Result.Success] containing the list of [LeagueSlug] items.
     * - [Result.Error] if the remote fetch fails.
     */
    fun getLeagueSlugs(): Flow<Result<List<LeagueSlug>>>

    /**
     * Retrieves standings tables for the specified list of [leagueSlugs].
     *
     * @param leagueSlugs The list of leagues to query.
     * @return A cold [Flow] emitting:
     * - [Result.Loading] when the fetch begins.
     * - [Result.Success] containing the list of successfully retrieved [LeagueStandings].
     * - [Result.Error] if [leagueSlugs] is empty or all requests fail.
     */
    fun getStandingsForLeagues(leagueSlugs: List<LeagueSlug>): Flow<Result<List<LeagueStandings>>>
}
