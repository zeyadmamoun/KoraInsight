package com.zeyadmamoun.core.datetime

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
