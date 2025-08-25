package com.blpw.pixelex.common.domain

sealed interface DataError {
    sealed interface Remote : DataError {
        data object RequestTimeout : Remote
        data object TooManyRequests : Remote
        data object NoInternet : Remote
        data class ServerError(val code: Int, val message: String?) : Remote // 5xx or specific code
        data object Unauthorized : Remote
        data object NotFound : Remote
        data object Serialization : Remote
        data object Unknown : Remote
    }

    sealed interface Local : DataError {
        data object DiskFull : Local
        data object Serialization : Local
        data object Unknown : Local
    }
}