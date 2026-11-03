package com.zeyadmamoun.core.common.result

sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val message: String? = null, val throwable: Throwable? = null) : Result<Nothing>
}

/**
 * Returns the encapsulated data if this instance is [Result.Success], or null otherwise.
 */
fun <T> Result<T>.dataOrNull(): T? = (this as? Result.Success)?.data

/**
 * Returns the encapsulated error message if this instance is [Result.Error], or null otherwise.
 */
fun <T> Result<T>.errorMessageOrNull(): String? = (this as? Result.Error)?.message
