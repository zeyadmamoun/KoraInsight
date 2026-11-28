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
 * Normalizes ISO date-time strings by ensuring seconds exist and UTC is assumed when no timezone offset is present.
 * Examples:
 * - "2026-11-24T20:00Z" -> "2026-11-24T20:00:00Z"
 * - "2026-11-24T20:00" -> "2026-11-24T20:00:00Z"
 * - "2026-11-24T20:00:00Z" -> "2026-11-24T20:00:00Z"
 */
fun normalizeIsoDateTime(isoString: String): String {
    val s = isoString.trim()
    if (!s.contains('T')) return s

    val datePart = s.substringBefore('T')
    val timeWithTz = s.substringAfter('T')

    val tzSuffix: String
    val timePart: String
    when {
        timeWithTz.endsWith("Z", ignoreCase = true) -> {
            tzSuffix = "Z"
            timePart = timeWithTz.dropLast(1)
        }
        timeWithTz.contains('+') -> {
            tzSuffix = "+" + timeWithTz.substringAfter('+')
            timePart = timeWithTz.substringBefore('+')
        }
        timeWithTz.contains('-') -> {
            tzSuffix = "-" + timeWithTz.substringAfter('-')
            timePart = timeWithTz.substringBefore('-')
        }
        else -> {
            tzSuffix = "Z" // Default soccer fixtures to UTC
            timePart = timeWithTz
        }
    }

    val timeSegments = timePart.split(':')
    val normalizedTime = when (timeSegments.size) {
        1 -> "${timeSegments[0]}:00:00"
        2 -> "${timeSegments[0]}:${timeSegments[1]}:00"
        else -> timePart
    }

    return "${datePart}T${normalizedTime}${tzSuffix}"
}

/**
 * Parses any ISO 8601 date string into an [Instant], normalizing missing seconds or timezone if needed.
 */
fun String.toIsoInstantOrNull(): Instant? {
    return try {
        Instant.parse(this.trim())
    } catch (_: Exception) {
        try {
            Instant.parse(normalizeIsoDateTime(this))
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Converts an ISO 8601 UTC date string (e.g. "2026-11-24T20:00Z") into local time.
 * Defaults to 12-hour format "h:mm a" (e.g. "10:00 PM") or "HH:mm" (e.g. "22:00") based on [use12Hour].
 */
fun String.toLocalTimeString(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    use12Hour: Boolean = true
): String {
    val instant = this.toIsoInstantOrNull() ?: return try {
        if (contains('T')) substringAfter('T').take(5) else "VS"
    } catch (_: Exception) {
        "VS"
    }

    val local = instant.toLocalDateTime(timeZone)
    val minute = local.minute.toString().padStart(2, '0')

    return if (use12Hour) {
        val hour12 = if (local.hour % 12 == 0) 12 else local.hour % 12
        val amPm = if (local.hour < 12) "AM" else "PM"
        "$hour12:$minute $amPm"
    } else {
        val hour24 = local.hour.toString().padStart(2, '0')
        "$hour24:$minute"
    }
}

/**
 * Returns the current time formatted as "h:mm a" (e.g., "7:36 AM") or "HH:mm".
 */
fun currentFormattedTime(use12Hour: Boolean = true): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val minute = now.minute.toString().padStart(2, '0')
    return if (use12Hour) {
        val hour12 = if (now.hour % 12 == 0) 12 else now.hour % 12
        val amPm = if (now.hour < 12) "AM" else "PM"
        "$hour12:$minute $amPm"
    } else {
        val hour24 = now.hour.toString().padStart(2, '0')
        "$hour24:$minute"
    }
}

/**
 * Formats an [Instant] as a compact relative timestamp such as `"Just now"`, `"12m ago"`,
 * `"2h ago"`, `"3d ago"`, or `"Nov 28"` for items older than 7 days.
 */
@OptIn(ExperimentalTime::class)
fun Instant.toRelativeTimeAgo(
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val diffSeconds = (now.epochSeconds - this.epochSeconds).coerceAtLeast(0L)
    val minutes = diffSeconds / 60L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 1L -> "Just now"
        hours < 1L -> "${minutes}m ago"
        days < 1L -> "${hours}h ago"
        days < 7L -> "${days}d ago"
        else -> toLocalDateTime(timeZone).date.toChipString().substringAfter(", ")
    }
}

