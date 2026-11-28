package com.zeyadmamoun.data.model.summary

import com.zeyadmamoun.network.model.summary.EspnMatchSummaryResponseDto
import com.zeyadmamoun.network.model.summary.EspnSummaryCompetitionDto
import com.zeyadmamoun.network.model.summary.EspnSummaryHeaderDto
import com.zeyadmamoun.network.model.summary.EspnSummaryStatusDto
import com.zeyadmamoun.network.model.summary.EspnSummaryStatusTypeDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchSummaryMapperTest {

    @Test
    fun `toMatchDetails maps live match with displayClock`() {
        val dto = summaryWithStatus(
            EspnSummaryStatusDto(
                displayClock = "81'",
                type = EspnSummaryStatusTypeDto(
                    state = "in",
                    completed = false,
                    description = "In Progress",
                    detail = "81'",
                    shortDetail = "81'"
                )
            )
        )

        val result = dto.toMatchDetails()

        assertTrue(result.isLive)
        assertEquals("81'", result.currentMinute)
        assertFalse(result.isCompleted)
    }

    @Test
    fun `toMatchDetails falls back to detail when displayClock is missing for live match`() {
        val dto = summaryWithStatus(
            EspnSummaryStatusDto(
                displayClock = null,
                type = EspnSummaryStatusTypeDto(
                    state = "in",
                    completed = false,
                    description = "In Progress",
                    detail = "64'",
                    shortDetail = "64'"
                )
            )
        )

        val result = dto.toMatchDetails()

        assertTrue(result.isLive)
        assertEquals("64'", result.currentMinute)
    }

    @Test
    fun `toMatchDetails returns HT for live halftime match`() {
        val dto = summaryWithStatus(
            EspnSummaryStatusDto(
                displayClock = "45'",
                type = EspnSummaryStatusTypeDto(
                    state = "in",
                    completed = false,
                    description = "Halftime",
                    detail = "HT",
                    shortDetail = "HT"
                )
            )
        )

        val result = dto.toMatchDetails()

        assertTrue(result.isLive)
        assertEquals("HT", result.currentMinute)
    }

    @Test
    fun `toMatchDetails sets isLive false and empty currentMinute for scheduled and completed matches`() {
        val scheduled = summaryWithStatus(
            EspnSummaryStatusDto(
                type = EspnSummaryStatusTypeDto(
                    state = "pre",
                    completed = false,
                    description = "Scheduled",
                    detail = "20:00",
                    shortDetail = "20:00"
                )
            )
        ).toMatchDetails()

        val completed = summaryWithStatus(
            EspnSummaryStatusDto(
                displayClock = "90'",
                type = EspnSummaryStatusTypeDto(
                    state = "post",
                    completed = true,
                    description = "Full Time",
                    detail = "FT",
                    shortDetail = "FT"
                )
            )
        ).toMatchDetails()

        assertFalse(scheduled.isLive)
        assertEquals("", scheduled.currentMinute)
        assertFalse(completed.isLive)
        assertEquals("", completed.currentMinute)
        assertTrue(completed.isCompleted)
    }

    @Test
    fun `toMatchDetails extracts kickoffTime from competition date`() {
        val dto = EspnMatchSummaryResponseDto(
            header = EspnSummaryHeaderDto(
                id = "401861164",
                competitions = listOf(
                    EspnSummaryCompetitionDto(
                        id = "401861164",
                        date = "2026-11-14T17:00Z"
                    )
                )
            )
        )

        val result = dto.toMatchDetails()

        assertTrue(result.kickoffTime.isNotBlank())
        assertEquals(result.kickoffTime, result.matchTime)
        assertTrue(result.kickoffTime.matches(Regex("""\d{1,2}:\d{2}(?:\s?(?:AM|PM))?""")))
    }

    @Test
    fun `toScoreText returns kickoffTime for scheduled match and score for live or completed match`() {
        val scheduled = MatchDetails(
            isLive = false,
            isCompleted = false,
            kickoffTime = "19:00",
            homeTeamScore = "0",
            awayTeamScore = "0"
        )
        val live = MatchDetails(
            isLive = true,
            isCompleted = false,
            kickoffTime = "19:00",
            homeTeamScore = "2",
            awayTeamScore = "1"
        )
        val finished = MatchDetails(
            isLive = false,
            isCompleted = true,
            kickoffTime = "19:00",
            homeTeamScore = "3",
            awayTeamScore = "1"
        )

        assertEquals("19:00", scheduled.toScoreText())
        assertEquals("19:00", scheduled.scoreText)
        assertEquals("2 - 1", live.toScoreText())
        assertEquals("3 - 1", finished.toScoreText())
    }

    private fun summaryWithStatus(status: EspnSummaryStatusDto): EspnMatchSummaryResponseDto {
        return EspnMatchSummaryResponseDto(
            header = EspnSummaryHeaderDto(
                id = "401879248",
                competitions = listOf(
                    EspnSummaryCompetitionDto(
                        id = "401879248",
                        status = status
                    )
                )
            )
        )
    }
}
