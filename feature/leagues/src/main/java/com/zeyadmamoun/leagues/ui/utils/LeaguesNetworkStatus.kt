package com.zeyadmamoun.leagues.ui.utils

sealed interface LeaguesNetworkStatus {
    data object Loading : LeaguesNetworkStatus
    data object Success : LeaguesNetworkStatus
    data class Error(val message: String) : LeaguesNetworkStatus
}