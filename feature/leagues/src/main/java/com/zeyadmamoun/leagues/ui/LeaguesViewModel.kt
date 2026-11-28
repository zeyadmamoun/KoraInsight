package com.zeyadmamoun.leagues.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.scoreboard.Match
import com.zeyadmamoun.data.model.standings.LeagueStandings
import com.zeyadmamoun.data.repository.LeaguesRepository
import com.zeyadmamoun.data.repository.MatchesRepository
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
    private val leaguesRepository: LeaguesRepository,
    private val matchesRepository: MatchesRepository
) : ViewModel() {

    private var _leaguesUiState = MutableStateFlow(LeaguesScreenState())
    val leaguesUiState: StateFlow<LeaguesScreenState> = _leaguesUiState

    init {
        getLeagues()
        getMatchesForLeague("uefa.champions")
    }

    fun getLeagues() {
        viewModelScope.launch {
            leaguesRepository.getLeagueSlugs().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _leaguesUiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
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
                                networkStatus = NetworkStatus.Error(
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
                        _leaguesUiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
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
                                networkStatus = NetworkStatus.Success
                            )
                        }
                    }
                    is Result.Error -> {
                        _leaguesUiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(
                                    result.message ?: "Failed to fetch standings"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the current matchday / active fixtures for any specific league or tournament.
     *
     * @param slug The ESPN league slug (e.g. "uefa.champions").
     */
    fun getMatchesForLeague(slug: String = "uefa.champions") {
        viewModelScope.launch {
            matchesRepository.getScoreboardForLeague(slug).collect { result ->
                if (result is Result.Success) {
                    _leaguesUiState.update { it.copy(uclMatches = result.data.matches) }
                }
            }
        }
    }
}

data class LeaguesScreenState(
    val leagues: List<LeagueSlug> = emptyList(),
    val standings: List<LeagueStandings> = emptyList(),
    val standingsCardData: StandingsCardData? = null,
    val uclMatches: List<Match> = emptyList(),
    val networkStatus: NetworkStatus = NetworkStatus.Loading
)
