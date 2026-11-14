package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.model.summary.toMatchDetails
import com.zeyadmamoun.network.ktor.NetworkDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchDetailsRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : MatchDetailsRepository {

    private val cachedMatchDetails = mutableMapOf<String, MatchDetails>()

    override fun getMatchDetails(
        matchId: String,
        leagueSlug: String,
        isForceRefresh: Boolean
    ): Flow<Result<MatchDetails>> = flow {
        if (matchId.isBlank()) {
            emit(Result.Error("Match ID cannot be blank"))
            return@flow
        }

        emit(Result.Loading)

        val cached = cachedMatchDetails[matchId]
        if (cached != null && !isForceRefresh) {
            emit(Result.Success(cached))
            return@flow
        }

        try {
            val response = networkDataSource.getMatchSummary(leagueSlug, matchId)
            val details = response.toMatchDetails()
            cachedMatchDetails[matchId] = details
            emit(Result.Success(details))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.Error(e.message ?: "Failed to fetch match details for match ID $matchId"))
        }
    }.flowOn(Dispatchers.IO)
}
