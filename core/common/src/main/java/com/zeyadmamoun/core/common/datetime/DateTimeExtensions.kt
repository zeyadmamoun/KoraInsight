package com.zeyadmamoun.core.common.datetime

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

/**
 * Returns today's [LocalDate] in the specified [timeZone] (defaults to [TimeZone.currentSystemDefault]).
 */
@OptIn(ExperimentalTime::class)
fun Clock.today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return now().toLocalDateTime(timeZone).date
}

/**
 * Formats this [LocalDate] to the ISO standard "yyyy-MM-dd" string (e.g. for API queries).
 */
fun LocalDate.toApiDateString(): String {
    return format(DateFormats.ISO_DATE)
}

/**
 * Formats this [LocalDate] to the compact "yyyyMMdd" string (e.g. for ESPN API queries `?dates=yyyyMMdd`).
 */
fun LocalDate.toCompactDateString(): String {
    return format(DateFormats.BASIC_ISO_DATE)
}

/**
 * Returns date formatted for navigation chips [dayOfTheWeek, monthName dayOfMonth]
 * (e.g. "Sun, Oct 25"). Uses pre-allocated [DateFormats.CHIP_DATE].
 */
fun LocalDate.toChipString(): String {
    return format(DateFormats.CHIP_DATE)
}

/**
 * Returns "Today", "Tomorrow", "Yesterday", or falls back to "Mon, Oct 26".
 */
fun LocalDate.toRelativeChipString(today: LocalDate = Clock.System.today()): String {
    val dayDifference = this.toEpochDays() - today.toEpochDays()
    return when (dayDifference.toInt()) {
        0 -> "Today"
        1 -> "Tomorrow"
        -1 -> "Yesterday"
        else -> this.toChipString() // Fallback to "Mon, Oct 26"
    }
}

/**
 * Converts an ISO calendar date string (e.g. "2027-06-01T03:59Z")
 * to compact request format "yyyyMMdd" (e.g. "20270601").
 */
fun String.calendarDateToCompactString(): String {
    val datePart = this.substringBefore('T')
    return LocalDate.parse(datePart).toCompactDateString()
}

/**
 * Converts an ISO 8601 UTC date string (e.g. "2026-10-31T12:30Z") into local time "HH:mm" (e.g. "14:30").
 */
fun String.toLocalTimeString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return try {
        val instant = Instant.parse(this)
        val local = instant.toLocalDateTime(timeZone)
        val hour = local.hour.toString().padStart(2, '0')
        val minute = local.minute.toString().padStart(2, '0')
        "$hour:$minute"
    } catch (_: Exception) {
        try {
            if (contains('T')) {
                substringAfter('T').take(5)
            } else {
                "VS"
            }
        } catch (_: Exception) {
            "VS"
        }
    }
}

