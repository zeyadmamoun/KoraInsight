package com.zeyadmamoun.core.common.result

/**
 * this is sealed interface used in the UiStates to control the state of the network request
 * and display the appropriate UI elements.
 * */
sealed interface NetworkStatus {
    data object Loading : NetworkStatus
    data object Success : NetworkStatus
    data class Error(val message: String) : NetworkStatus
}