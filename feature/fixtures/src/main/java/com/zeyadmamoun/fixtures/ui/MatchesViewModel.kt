package com.zeyadmamoun.fixtures.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.datetime.toCompactDateString
import com.zeyadmamoun.core.common.datetime.today
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.scoreboard.Scoreboard
import com.zeyadmamoun.data.repository.LeaguesRepository
import com.zeyadmamoun.data.repository.MatchesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import javax.inject.Inject
import kotlin.time.Clock

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchesRepository: MatchesRepository,
    private val leaguesRepository: LeaguesRepository
) : ViewModel() {

    val todayDate
        get() = Clock.System.today()

    private var fetchMatchesJob: Job? = null

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
        _matchesUiState.update { state ->
            val currentIndex = state.nearbyDays.indexOf(day)
            val updatedDays = if (currentIndex != -1) {
                if (currentIndex >= state.nearbyDays.size - 2) {
                    val lastDay = state.nearbyDays.last()
                    (state.nearbyDays + getFiveMoreDays(lastDay)).distinctBy { it.toEpochDays() }
                } else if (currentIndex <= 1) {
                    val firstDay = state.nearbyDays.first()
                    (getFivePreviousDays(firstDay) + state.nearbyDays).distinctBy { it.toEpochDays() }
                } else {
                    state.nearbyDays
                }
            } else {
                state.nearbyDays
            }

            state.copy(
                selectedDay = day,
                nearbyDays = updatedDays,
                networkStatus = NetworkStatus.Loading
            )
        }
        getScoreboards()
    }

    fun refresh() {
        _matchesUiState.update { it.copy(isRefreshing = true) }
        getLeagues(isRefresh = true)
    }

    fun getLeagues(isRefresh: Boolean = false) {
        viewModelScope.launch {
            leaguesRepository.getLeagueSlugs().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        if (!isRefresh) {
                            _matchesUiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
                        }
                    }

                    is Result.Success -> {
                        _matchesUiState.update {
                            it.copy(
                                leagues = result.data,
                            )
                        }
                        getScoreboards(isRefresh = isRefresh)
                    }

                    is Result.Error -> {
                        _matchesUiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(
                                    result.message ?: "Failed to fetch leagues"
                                ),
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun getScoreboards(isRefresh: Boolean = false) {
        fetchMatchesJob?.cancel()
        if (!isRefresh) {
            _matchesUiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
        }

        fetchMatchesJob = viewModelScope.launch {
            matchesRepository.getMatchesForLeague(
                _matchesUiState.value.leagues,
                _matchesUiState.value.selectedDay.toCompactDateString(),
                isRefresh
            ).collect { result ->

                when (result) {
                    is Result.Loading -> {
                        if (!isRefresh) {
                            _matchesUiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
                        }
                    }

                    is Result.Success<List<Scoreboard>> -> {
                        val activeScoreboards = result.data.filter { scoreboard ->
                            scoreboard.matches.isNotEmpty()
                        }
                        _matchesUiState.update {
                            it.copy(
                                scoreboards = activeScoreboards,
                                networkStatus = NetworkStatus.Success,
                                isRefreshing = false
                            )
                        }
                    }

                    is Result.Error -> {
                        _matchesUiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(result.message ?: "Failed to fetch matches"),
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MatchesUiState(
    val nearbyDays: List<LocalDate> = emptyList(),
    val selectedDay: LocalDate = Clock.System.today(),
    val leagues: List<LeagueSlug> = emptyList(),
    val scoreboards: List<Scoreboard> = emptyList(),
    val networkStatus: NetworkStatus = NetworkStatus.Loading,
    val isRefreshing: Boolean = false
)
