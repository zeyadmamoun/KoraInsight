package com.zeyadmamoun.leagues.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings
import com.zeyadmamoun.data.repository.LeaguesRepository
import com.zeyadmamoun.leagues.ui.utils.LeaguesNetworkStatus
import com.zeyadmamoun.leagues.ui.utils.StandingsCardData
import com.zeyadmamoun.leagues.ui.utils.toCardLeagues
import com.zeyadmamoun.leagues.ui.utils.toCardStandings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor(
    private val leaguesRepository: LeaguesRepository
) : ViewModel() {

    private var _leaguesUiState = MutableStateFlow(LeaguesScreenState())
    val leaguesUiState: StateFlow<LeaguesScreenState> = _leaguesUiState

    init {
        getLeagues()
    }

    fun getLeagues() {
        viewModelScope.launch {
            leaguesRepository.getLeagueSlugs().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _leaguesUiState.update { it.copy(networkStatus = LeaguesNetworkStatus.Loading) }
                    }

                    is Result.Success -> {
                        _leaguesUiState.update {
                            it.copy(
                                leagues = result.data,
                            )
                        }
                        getLeagueStandings(result.data)
                    }

                    is Result.Error -> {
                        _leaguesUiState.update {
                            it.copy(
                                networkStatus = LeaguesNetworkStatus.Error(
                                    result.message ?: "Failed to fetch leagues"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun getLeagueStandings(leagueSlugs: List<LeagueSlug>) {
        viewModelScope.launch {
            leaguesRepository.getStandingsForLeagues(leagueSlugs).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _leaguesUiState.update { it.copy(networkStatus = LeaguesNetworkStatus.Loading) }
                    }
                    is Result.Success -> {
                        val cardLeagues = leagueSlugs.toCardLeagues()
                        _leaguesUiState.update {
                            it.copy(
                                standings = result.data,
                                standingsCardData = StandingsCardData(
                                    cardLeagues = cardLeagues,
                                    standings = result.data.toCardStandings(cardLeagues)
                                ),
                                networkStatus = LeaguesNetworkStatus.Success
                            )
                        }
                    }
                    is Result.Error -> {
                        _leaguesUiState.update {
                            it.copy(
                                networkStatus = LeaguesNetworkStatus.Error(
                                    result.message ?: "Failed to fetch standings"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

data class LeaguesScreenState(
    val leagues: List<LeagueSlug> = emptyList(),
    val standings: List<LeagueStandings> = emptyList(),
    val standingsCardData: StandingsCardData? = null,
    val networkStatus: LeaguesNetworkStatus = LeaguesNetworkStatus.Loading
)