package com.blpw.pixelex.features.stackUserList.data.local

object ApiBackoff {
    private val nextAllowedAt = java.util.concurrent.atomic.AtomicLong(0)
    fun remainingMs(): Long = (nextAllowedAt.get() - android.os.SystemClock.elapsedRealtime()).coerceAtLeast(0)
    fun setBackoffSeconds(sec: Int) {
        nextAllowedAt.set(android.os.SystemClock.elapsedRealtime() + sec * 1000L)
    }
}