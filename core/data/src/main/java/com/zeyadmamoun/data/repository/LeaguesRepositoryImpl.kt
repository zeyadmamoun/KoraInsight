package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.scoreboard.toLeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings
import com.zeyadmamoun.data.model.standings.toLeagueStandings
import com.zeyadmamoun.network.ktor.NetworkDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [LeaguesRepository] that manages league discovery and standings data.
 *
 * Interacts with [NetworkDataSource] to:
 * 1. Retrieve featured leagues from Supabase with an in-memory cache to prevent redundant fetches.
 * 2. Retrieve standings tables from ESPN across multiple leagues concurrently using structured concurrency
 *    ([coroutineScope] and [async]), safely isolating failures on a per-league basis.
 *
 * All flows are dispatched on [Dispatchers.IO] to keep operations off the main thread.
 *
 * @property networkDataSource The remote data source used for Supabase and ESPN network calls.
 */
@Singleton
class LeaguesRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : LeaguesRepository {

    /**
     * In-memory cache holding the fetched [LeagueSlug] list for the lifetime of this repository instance.
     * Prevents repeated network queries on configuration changes or screen re-visits.
     */
    val cachedLeagueSlugs = mutableListOf<LeagueSlug>()

    /**
     * Retrieves the list of featured leagues supported by the app.
     *
     * Follows a cache-first approach:
     * - Emits [Result.Loading].
     * - Returns [cachedLeagueSlugs] immediately if previously fetched.
     * - Otherwise queries [NetworkDataSource.getLeagues], populates the cache, and emits [Result.Success].
     * - Emits [Result.Error] if the remote fetch fails, preserving [CancellationException] for coroutine cancellation.
     *
     * @return A cold [Flow] emitting the [Result] of the operation.
     */
    override fun getLeagueSlugs(): Flow<Result<List<LeagueSlug>>> = flow {
        emit(Result.Loading)

        // 1. Return cached leagues immediately if available
        if (cachedLeagueSlugs.isNotEmpty()) {
            emit(Result.Success(cachedLeagueSlugs))
            return@flow
        }

        // 2. Fetch fresh leagues from remote source and populate memory cache
        try {
            val response = networkDataSource.getLeagues()
            val mapped = response.map { it.toLeagueSlug() }
            cachedLeagueSlugs.addAll(mapped)
            emit(Result.Success(mapped))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fetches league standings concurrently for the provided list of [leagueSlugs].
     *
     * Execution flow:
     * - Emits [Result.Error] immediately if [leagueSlugs] is empty.
     * - Emits [Result.Loading].
     * - Spawns concurrent [async] coroutines within a [coroutineScope] for each league.
     * - Absorbs individual league failures (e.g., network timeout or invalid slug) by returning `null`,
     *   allowing successful league responses to still be displayed.
     * - Emits [Result.Success] containing all fetched [LeagueStandings], or [Result.Error] if every request failed.
     *
     * @param leagueSlugs The list of leagues whose standings should be fetched.
     * @return A cold [Flow] emitting the [Result] containing the list of [LeagueStandings].
     */
    override fun getStandingsForLeagues(leagueSlugs: List<LeagueSlug>): Flow<Result<List<LeagueStandings>>> =
        flow<Result<List<LeagueStandings>>> {
            // 1. Guard against empty requests before emitting loading state
            if (leagueSlugs.isEmpty()) {
                emit(Result.Error("No leagues selected"))
                return@flow
            }

            emit(Result.Loading)

            // 2. Query ESPN standings concurrently while isolating failures per league
            val standingsList = coroutineScope {
                leagueSlugs.map { slug ->
                    async {
                        try {
                            networkDataSource.getStandings(slug.slug).toLeagueStandings()
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            e.printStackTrace()
                            null // Absorbs failure so other leagues continue uninterrupted
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            // 3. Emit success if at least one league succeeded, otherwise error
            if (standingsList.isEmpty()) {
                emit(Result.Error("Failed to fetch standings for all leagues"))
            } else {
                emit(Result.Success(standingsList))
            }
        }.flowOn(Dispatchers.IO)

    /**
     * Retrieves the standings table for a single [leagueSlug].
     *
     * @param leagueSlug The league slug identifier (e.g. "eng.1", "esp.1").
     * @return A cold [Flow] emitting [Result.Loading], [Result.Success], or [Result.Error].
     */
    override fun getStandings(leagueSlug: String): Flow<Result<LeagueStandings>> = flow {
        if (leagueSlug.isBlank()) {
            emit(Result.Error("League slug cannot be blank"))
            return@flow
        }
        emit(Result.Loading)
        try {
            val standings = networkDataSource.getStandings(leagueSlug).toLeagueStandings()
            emit(Result.Success(standings))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.Error(e.message ?: "Failed to fetch standings for $leagueSlug"))
        }
    }.flowOn(Dispatchers.IO)
}
