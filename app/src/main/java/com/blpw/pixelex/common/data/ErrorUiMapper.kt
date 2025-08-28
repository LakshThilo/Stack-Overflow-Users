package com.blpw.pixelex.common.data

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RateLimitBackoffException(val retryAfterSeconds: Int?) : Exception()
class NetworkFailure : Exception()
class TimeoutFailure : Exception()
class ServerFailure(val code: Int, msg: String?) : Exception(msg)

fun Throwable.toUserMessage(): String = when (this) {
    is RateLimitBackoffException ->
        "Rate limited. Please wait ${retryAfterSeconds}s and try again."
    is UnknownHostException ->
        "You’re offline. Check your internet connection."
    is SocketTimeoutException ->
        "Network is slow. Please try again."
    is IOException ->
        "Network error. Please try again."
    is retrofit2.HttpException -> when (code()) {
        429 -> "Too many requests. Please wait and try again."
        in 500..599 -> "Server is having issues. Try again later."
        401, 403 -> "Access denied. Check your API key."
        else -> "Server error (${code()}). Please try again."
    }
    is com.squareup.moshi.JsonDataException,
    is com.google.gson.JsonSyntaxException -> "Data error. Please try again."
    else -> localizedMessage ?: "Something went wrong."
}