package com.zeyadmamoun.core.common.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames

/**
 * Standard date and time formatters across the KoraInsight application.
 */
object DateFormats {
    /**
     * ISO-8601 extended local date format: "yyyy-MM-dd" (e.g., "2026-10-25").
     * Used for API queries (e.g. Highlightly `/matches?date=yyyy-MM-dd`) and internal persistence.
     */
    val ISO_DATE = LocalDate.Formats.ISO

    /**
     * ISO-8601 basic compact date format: "yyyyMMdd" (e.g., "20261101").
     * Used for ESPN API queries (e.g. `/scoreboard?dates=yyyyMMdd`) and compact date parameters.
     */
    val BASIC_ISO_DATE = LocalDate.Format {
        year()
        monthNumber()
        day()
    }

    /**
     * Alias for [BASIC_ISO_DATE] ("yyyyMMdd").
     */
    val COMPACT_DATE = BASIC_ISO_DATE

    /**
     * Chip date format: "EEE, MMM d" (e.g., "Sun, Oct 25").
     * Pre-allocated to avoid constructing formatter builders on every recomposition.
     */
    val CHIP_DATE = LocalDate.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        chars(" ")
        day()
    }
}
