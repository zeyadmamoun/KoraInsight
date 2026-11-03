package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.LeagueSlug
import com.zeyadmamoun.data.model.Scoreboard
import com.zeyadmamoun.data.model.toLeagueSlug
import com.zeyadmamoun.data.model.toScoreboard
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

/**
 * Default implementation of [MatchesRepository] delegating to [NetworkDataSource].
 *
 * Employs structured concurrency with [coroutineScope] and [async] to execute multi-league
 * queries in parallel without blocking worker threads, safely isolating single-league errors.
 *
 * @property networkDataSource The remote network client (powered by Ktor).
 */
class MatchRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : MatchesRepository {

    override fun getLeagueSlugs(): Flow<Result<List<LeagueSlug>>> = flow {
        emit(Result.Loading)
        try {
            val response = networkDataSource.getLeagues()
            emit(Result.Success(response.map { it.toLeagueSlug() }))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)


    override fun getMatchesForLeague(
        leagueSlugs: List<LeagueSlug>,
        date: String
    ): Flow<Result<List<Scoreboard>>> = flow {
        // 1. Guard against empty requests before emitting loading state
        if (leagueSlugs.isEmpty()) {
            emit(Result.Error("No leagues selected"))
            return@flow
        }

        emit(Result.Loading)

        // 2. Fire requests concurrently while isolating failures per league
        val scoreboardList = coroutineScope {
            leagueSlugs.map { league ->
                async {
                    try {
                        val response = networkDataSource.getScoreboards(league.slug, date)
                        response.toScoreboard()
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        e.printStackTrace()
                        null // Absorbs failure so other leagues continue uninterrupted
                    }
                }
            }.awaitAll().filterNotNull()
        }

        // 3. Emit success with partial or full results, or error if all failed
        if (scoreboardList.isNotEmpty()) {
            emit(Result.Success(scoreboardList))
        } else {
            emit(Result.Error("Failed to fetch matches for all leagues"))
        }
    }.flowOn(Dispatchers.IO)
}
