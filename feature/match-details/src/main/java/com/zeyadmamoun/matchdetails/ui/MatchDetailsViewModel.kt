package com.zeyadmamoun.matchdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.repository.MatchDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MatchDetailsUiState {
    data object Loading : MatchDetailsUiState
    data class Success(val matchDetails: MatchDetails) : MatchDetailsUiState
    data class Error(val message: String) : MatchDetailsUiState
}

@HiltViewModel
class MatchDetailsViewModel @Inject constructor(
    private val matchDetailsRepository: MatchDetailsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchDetailsUiState>(MatchDetailsUiState.Loading)
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
                    is Result.Loading -> _uiState.value = MatchDetailsUiState.Loading
                    is Result.Success -> _uiState.value = MatchDetailsUiState.Success(result.data)
                    is Result.Error -> _uiState.value = MatchDetailsUiState.Error(
                        result.message ?: "Failed to load match details"
                    )
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
