package com.blpw.pixelex.common.data

import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi

suspend inline fun <T> safeApi(moshi: Moshi, crossinline block: suspend () -> T): Result<T, DataError.Remote> =
    try {
        Result.Success(block())
    } catch (t: Throwable) {
        if (t is JsonDataException || t is JsonEncodingException) {
            android.util.Log.e("Moshi", "Decoding failed", t)
        }
        Result.Error(t.toRemoteError(moshi))
    }

suspend inline fun <T> safeLocal(crossinline block: suspend () -> T): Result<T, DataError.Local> =
    try {
        Result.Success(block())
    } catch (t: Throwable) {
        Result.Error(t.toLocalError())
    }