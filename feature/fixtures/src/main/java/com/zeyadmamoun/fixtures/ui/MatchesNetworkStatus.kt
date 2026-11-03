package com.zeyadmamoun.fixtures.ui

import com.zeyadmamoun.data.model.Scoreboard

sealed interface MatchesNetworkStatus {
    data object Loading : MatchesNetworkStatus
    data class Success(val scoreboards: List<Scoreboard>) : MatchesNetworkStatus
    data class Error(val message: String) : MatchesNetworkStatus
}
