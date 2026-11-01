package com.zeyadmamoun.fixtures.ui

import com.zeyadmamoun.network.model.EspnApiResponse

sealed interface MatchesNetworkStatus {
    data object Loading : MatchesNetworkStatus
    data class Success(val scoreboards: List<EspnApiResponse>) : MatchesNetworkStatus
    data class Error(val message: String) : MatchesNetworkStatus
}
