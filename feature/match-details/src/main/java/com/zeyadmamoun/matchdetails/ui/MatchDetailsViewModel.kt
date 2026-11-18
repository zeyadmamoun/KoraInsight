package com.zeyadmamoun.matchdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.repository.MatchDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchDetailsUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Loading,
    val matchDetails: MatchDetails? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class MatchDetailsViewModel @Inject constructor(
    private val matchDetailsRepository: MatchDetailsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchDetailsUiState())
    val uiState: StateFlow<MatchDetailsUiState> = _uiState.asStateFlow()

    private var currentMatchId: String = ""
    private var currentLeagueSlug: String = ""

    fun loadMatchDetails(
        matchId: String,
        leagueSlug: String = "",
        isForceRefresh: Boolean = false
    ) {
        if (matchId.isBlank()) return
        currentMatchId = matchId
        currentLeagueSlug = leagueSlug

        viewModelScope.launch {
            matchDetailsRepository.getMatchDetails(matchId, leagueSlug, isForceRefresh).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update {
                            it.copy(networkStatus = NetworkStatus.Loading)
                        }
                    }

                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Success,
                                matchDetails = result.data
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(
                                    result.message ?: "Failed to fetch match details"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        if (currentMatchId.isNotBlank()) {
            loadMatchDetails(currentMatchId, currentLeagueSlug, isForceRefresh = true)
        }
    }
}
