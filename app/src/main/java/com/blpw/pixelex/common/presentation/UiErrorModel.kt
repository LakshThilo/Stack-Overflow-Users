package com.blpw.pixelex.common.presentation

import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.ui.graphics.vector.ImageVector
import com.blpw.pixelex.common.domain.DataError.Local
import com.blpw.pixelex.common.domain.DataError.Remote

data class UiErrorModel(
    val icon: ImageVector,
    val title: String,
    val message: String = ""
)

fun Any?.toUiError(): UiErrorModel = when (this) {
    is Remote -> this.toUiError()
    is Local -> this.toUiError()
    is Throwable -> UiErrorModel(
        icon = Outlined.ErrorOutline,
        title = "Unexpected error",
        message = localizedMessage ?: toString()
    )

    is String -> UiErrorModel(
        icon = Outlined.ErrorOutline,
        title = "Something went wrong",
        message = this
    )

    else -> UiErrorModel(
        icon = Outlined.ErrorOutline,
        title = "Something went wrong",
        message = ""
    )
}

private fun Remote.toUiError(): UiErrorModel = when (this) {
    Remote.NoInternet -> UiErrorModel(
        Outlined.CloudOff,
        "You’re offline",
        "Check your connection and try again."
    )

    Remote.RequestTimeout -> UiErrorModel(
        Outlined.HourglassEmpty,
        "Request timed out",
        "The server took too long to respond."
    )

    Remote.TooManyRequests -> UiErrorModel(
        Outlined.PriorityHigh,
        "Too many requests",
        "Please wait a moment and try again."
    )

    Remote.Unauthorized -> UiErrorModel(
        Outlined.Lock,
        "Unauthorized",
        "Please sign in and try again."
    )

    Remote.NotFound -> UiErrorModel(
        Outlined.ErrorOutline,
        "Not found",
        "The resource you requested isn’t available."
    )

    is Remote.ServerError -> UiErrorModel(
        Outlined.ErrorOutline,
        title = "Server error${" ($code)"}",
        message = message.orEmpty()
    )

    Remote.Serialization -> UiErrorModel(
        Outlined.ErrorOutline,
        "Data format changed",
        "We couldn't read the server response."
    )

    Remote.Unknown -> UiErrorModel(
        Outlined.ErrorOutline,
        "Unexpected error",
        "Please try again."
    )
}

private fun Local.toUiError(): UiErrorModel = when (this) {
    Local.DiskFull -> UiErrorModel(
        Outlined.ErrorOutline,
        "Storage full",
        "Free up some space and try again."
    )

    Local.Serialization -> UiErrorModel(
        Outlined.ErrorOutline,
        "Corrupted data",
        "We couldn't read local data."
    )

    Local.Unknown -> UiErrorModel(
        Outlined.ErrorOutline,
        "Unexpected error",
        "Please try again."
    )
}