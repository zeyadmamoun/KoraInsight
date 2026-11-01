package com.zeyadmamoun.fixtures.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.datetime.toCompactDateString
import com.zeyadmamoun.core.common.datetime.today
import com.zeyadmamoun.network.ktor.KtorClient
import com.zeyadmamoun.network.model.EspnApiResponse
import com.zeyadmamoun.network.model.LeagueSlugDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Clock

class MatchesViewModel : ViewModel() {

    val todayDate
        get() = Clock.System.today()

    private val selectedDayIndex: Int
        get() = _matchesUiState.value.nearbyDays.indexOf(_matchesUiState.value.selectedDay)

    private var _matchesUiState = MutableStateFlow(MatchesUiState())
    val matchesUiState: StateFlow<MatchesUiState> = _matchesUiState

    init {
        val upcomingDays = getFiveMoreDays(todayDate)
        val previousDays = getFivePreviousDays(todayDate)
        _matchesUiState.update {
            it.copy(
                nearbyDays = previousDays + todayDate + upcomingDays
            )
        }
        getLeagues()
    }

    private fun getFiveMoreDays(dayDate: LocalDate): List<LocalDate> =
        (1..5).map { number -> dayDate.plus(number, kotlinx.datetime.DateTimeUnit.DAY) }

    private fun getFivePreviousDays(dayDate: LocalDate): List<LocalDate> =
        (1..5).map { number -> dayDate.minus(number, kotlinx.datetime.DateTimeUnit.DAY) }.reversed()

    fun updateSelectedDay(day: LocalDate) {
        _matchesUiState.update { value ->
            value.copy(
                selectedDay = day,
                networkStatus = MatchesNetworkStatus.Loading
            )
        }
        shouldLoadMoreDays()
        getScoreboards()
    }

    private fun shouldLoadMoreDays() {
        _matchesUiState.update {
            it.copy(
                nearbyDays = if (selectedDayIndex >= it.nearbyDays.size - 2) {
                    val lastDay = it.nearbyDays.last()
                    it.nearbyDays + getFiveMoreDays(lastDay)
                } else if (selectedDayIndex <= 1) {
                    val firstDay = it.nearbyDays.first()
                    getFivePreviousDays(firstDay) + it.nearbyDays
                } else {
                    it.nearbyDays
                }
            )
        }
    }

    fun getLeagues() {
        _matchesUiState.update { it.copy(networkStatus = MatchesNetworkStatus.Loading) }
        viewModelScope.launch {
            try {
                val data = KtorClient.getLeagues()
                _matchesUiState.update {
                    it.copy(
                        leagues = data ?: emptyList()
                    )
                }
                getScoreboards()
            } catch (e: Exception) {
                Log.e("MatchesViewModel", "Error fetching leagues", e)
                _matchesUiState.update {
                    it.copy(
                        networkStatus = MatchesNetworkStatus.Error(e.message ?: "Failed to fetch leagues")
                    )
                }
            }
        }
    }

    fun getMatchesFlow() = flow {
        val leagues = _matchesUiState.value.leagues
        if (leagues.isEmpty()) return@flow

        for (league in leagues) {
            val scoreboard = KtorClient.getScoreboards(
                league.slug,
                _matchesUiState.value.selectedDay.toCompactDateString()
            )
            scoreboard?.let {
                emit(it)
            }
        }
    }

    fun getScoreboards() {
        _matchesUiState.update { it.copy(networkStatus = MatchesNetworkStatus.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scoreboardsList = mutableListOf<EspnApiResponse>()
                getMatchesFlow().collect { scoreboard ->
                    scoreboardsList.add(scoreboard)
                }

                _matchesUiState.update {
                    it.copy(
                        scoreboards = scoreboardsList,
                        networkStatus = MatchesNetworkStatus.Success(scoreboardsList)
                    )
                }
                Log.d("MatchesScreen", "getScoreboards: ${scoreboardsList.size}")
            } catch (e: Exception) {
                Log.e("MatchesViewModel", "Error fetching scoreboards", e)
                _matchesUiState.update {
                    it.copy(
                        networkStatus = MatchesNetworkStatus.Error(e.message ?: "Failed to fetch matches")
                    )
                }
            }
        }
    }
}

data class MatchesUiState(
    val nearbyDays: List<LocalDate> = emptyList(),
    val selectedDay: LocalDate = Clock.System.today(),
    val leagues: List<LeagueSlugDto> = emptyList(),
    val scoreboards: List<EspnApiResponse> = emptyList(),
    val networkStatus: MatchesNetworkStatus = MatchesNetworkStatus.Loading
)
