package com.blpw.pixelex.common.domain

sealed interface Result<out D, out E : DataError> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : DataError>(val error: E) : Result<Nothing, E>
}

inline fun <T, E : DataError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
    when (this) { is Result.Success -> Result.Success(map(data)); is Result.Error -> this }

inline fun <T, E : DataError> Result<T, E>.onSuccess(block: (T) -> Unit): Result<T, E> =
    when (this) { is Result.Success -> also { block(data) }; is Result.Error -> this }

inline fun <T, E : DataError> Result<T, E>.onError(block: (E) -> Unit): Result<T, E> =
    when (this) { is Result.Error -> also { block(error) }; is Result.Success -> this }