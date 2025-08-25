package com.blpw.pixelex.common.presentation

import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
private fun Long.toInstantAuto(): Instant =
    if (this > 9_000_000_000L) Instant.ofEpochMilli(this) else Instant.ofEpochSecond(this)

@RequiresApi(Build.VERSION_CODES.O)
fun Long.memberFor(
    zone: ZoneId = ZoneId.systemDefault(),
    now: LocalDate = LocalDate.now(zone)
): String {
    val start = toInstantAuto().atZone(zone).toLocalDate()
    if (start.isAfter(now)) return "Member for 0 months"
    val p = Period.between(start, now)
    val years = p.years
    val months = p.months
    val parts = buildList {
        if (years > 0) add("$years year" + if (years != 1) "s" else "")
        if (months > 0) add("$months month" + if (months != 1) "s" else "")
    }
    return "Member for " + (if (parts.isEmpty()) "0 months" else parts.joinToString(", "))
}

@RequiresApi(Build.VERSION_CODES.O)
fun Int.memberFor(
    zone: ZoneId = ZoneId.systemDefault(),
    now: LocalDate = LocalDate.now(zone)
): String = this.toLong().memberFor(zone, now)

fun Int.lastOnlineText(): String {
    val timeMs = this.toLong() * 1000L
    return DateUtils.getRelativeTimeSpanString(
        timeMs,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

fun String.toDisplayUrl(): String = try {
    val uri = Uri.parse(this)
    val host = (uri.host ?: this).removePrefix("www.")
    val path = uri.encodedPath?.takeIf { it != "/" } ?: ""
    "$host$path"
} catch (_: Exception) {
    this
}