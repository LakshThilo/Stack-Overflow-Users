package com.blpw.pixelex.common.data

import com.blpw.pixelex.common.domain.DataError.Local
import com.blpw.pixelex.common.domain.DataError.Remote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class StackErrorBody(
    @Json(name = "error_id") val id: Int?,
    @Json(name = "error_message") val message: String?,
    @Json(name = "error_name") val name: String?
)

fun Throwable.toRemoteError(moshi: Moshi): Remote = when (this) {
    is SocketTimeoutException -> Remote.RequestTimeout
    is UnknownHostException -> Remote.NoInternet
    is HttpException -> this.toHttpRemoteError(moshi)
    is JsonDataException, is JsonEncodingException -> Remote.Serialization
    is IOException -> Remote.NoInternet
    else -> Remote.Unknown
}

private fun HttpException.requestUrl(): String? =
    response()?.raw()?.request?.url?.toString()

fun HttpException.toHttpRemoteError(moshi: Moshi): Remote {
    val code = code()
    val url  = requestUrl()
    val raw  = runCatching { response()?.errorBody()?.string().orEmpty() }.getOrDefault("")

    val parsed = runCatching {
        moshi.adapter(StackErrorBody::class.java).fromJson(raw)
    }.getOrNull()

    val parsedMsg = parsed?.message?.takeIf { it.isNotBlank() }
    val parsedName = parsed?.name
    val parsedId = parsed?.id

    val respMsg = response()?.message().takeUnless { it.isNullOrBlank() }
    val exMsg   = message().takeUnless { it.isNullOrBlank() }
    val snippet = raw.take(200).replace(Regex("\\s+"), " ").trim().takeIf { it.isNotBlank() }

    val msg = buildString {
        append(parsedMsg ?: respMsg ?: exMsg ?: "HTTP $code")
        if (!parsedName.isNullOrBlank()) append(" [$parsedName]")
        if (parsedId != null) append(" (#$parsedId)")
        if (parsedMsg == null && snippet != null) append(" | $snippet")
        if (!url.isNullOrBlank()) append(" | ").append(url)
    }

    return when (code) {
        401 -> Remote.Unauthorized
        404 -> Remote.NotFound
        408 -> Remote.RequestTimeout
        429 -> Remote.TooManyRequests
        in 400..599 -> Remote.ServerError(code, msg)
        else -> Remote.ServerError(code, msg)
    }
}

fun Throwable.toLocalError(): Local = when (this) {
    is JsonDataException, is JsonEncodingException -> Local.Serialization
    is IOException -> Local.DiskFull
    else -> Local.Unknown
}

