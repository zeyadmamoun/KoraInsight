package com.zeyadmamoun.fixtures.ui.utils

sealed interface MatchesNetworkStatus {
    data object Loading : MatchesNetworkStatus
    data object Success : MatchesNetworkStatus
    data class Error(val message: String) : MatchesNetworkStatus
}