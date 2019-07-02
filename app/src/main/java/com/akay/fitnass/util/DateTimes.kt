package com.akay.fitnass.util

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimes {
    private val showLocal = true

    fun getZoneId(): ZoneId = ZoneId.systemDefault()

    fun fromMs(ms: Long): ZonedDateTime {
        val instant = Instant.ofEpochMilli(ms)
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun toMs(zdt: ZonedDateTime?): Long {
        return zdt?.toInstant()?.toEpochMilli() ?: 0
    }

    fun nowMillis(): Long {
        return toMs(ZonedDateTime.now())
    }

    fun msToStrFormat(ms: Long): String {
        val msView = (ms % 1000L).toInt() / 10
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(ms) % 60).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(ms) % 60).toInt()
        val hours = (TimeUnit.MILLISECONDS.toHours(ms) % 24).toInt()
        val days = TimeUnit.MILLISECONDS.toDays(ms).toInt()

        return setFormat(days, hours, minutes, seconds, msView)
    }

    private fun setFormat(days: Int, hr: Int, min: Int, sec: Int, ms: Int): String {
        return when {
            days > 0 ->
                String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d.%02d", days, hr, min, sec, ms)
            hr > 0 ->
                String.format(Locale.getDefault(), "%02d.%02d.%02d.%02d", hr, min, sec, ms)
            else ->
                String.format(Locale.getDefault(), "%02d.%02d.%02d", min, sec, ms)
        }
    }
}
