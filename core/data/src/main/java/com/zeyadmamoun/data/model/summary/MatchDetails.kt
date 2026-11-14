package com.zeyadmamoun.data.model.summary

/**
 * Highly flattened domain model representing complete match details.
 *
 * All deeply nested properties from ESPN's response are flattened
 * directly onto this model for effortless UI binding and AI prompt generation.
 */
data class MatchDetails(
    val id: String = "",
    val date: String = "",
    val kickoffTime: String = "",
    val leagueName: String = "",
    val leagueSlug: String = "",
    val statusDescription: String = "",
    val statusDetail: String = "",
    val isLive: Boolean = false,
    val currentMinute: String = "",
    val isCompleted: Boolean = false,

    // Home Team Info
    val homeTeamId: String = "",
    val homeTeamName: String = "",
    val homeTeamDisplayName: String = "",
    val homeTeamAbbreviation: String = "",
    val homeTeamLogo: String = "",
    val homeTeamColor: String = "",
    val homeTeamScore: String = "0",
    val isHomeWinner: Boolean = false,

    // Away Team Info
    val awayTeamId: String = "",
    val awayTeamName: String = "",
    val awayTeamDisplayName: String = "",
    val awayTeamAbbreviation: String = "",
    val awayTeamLogo: String = "",
    val awayTeamColor: String = "",
    val awayTeamScore: String = "0",
    val isAwayWinner: Boolean = false,

    // Venue & Environment Info
    val venueName: String = "",
    val venueCity: String = "",
    val attendance: Int? = null,

    // Core Match Stats (Home vs Away)
    val homePossession: String = "50%",
    val awayPossession: String = "50%",
    val homeTotalShots: String = "0",
    val awayTotalShots: String = "0",
    val homeShotsOnTarget: String = "0",
    val awayShotsOnTarget: String = "0",
    val homeFouls: String = "0",
    val awayFouls: String = "0",
    val homeYellowCards: String = "0",
    val awayYellowCards: String = "0",
    val homeRedCards: String = "0",
    val awayRedCards: String = "0",
    val homeCorners: String = "0",
    val awayCorners: String = "0",
    val homeSaves: String = "0",
    val awaySaves: String = "0",
    val homePasses: String = "0",
    val awayPasses: String = "0",
    val homePassAccuracy: String = "0%",
    val awayPassAccuracy: String = "0%",

    // Tactical Formations
    val homeFormation: String? = null,
    val awayFormation: String? = null,

    // Collections
    val allStats: List<MatchStatItem> = emptyList(),
    val events: List<MatchTimelineEvent> = emptyList(),
    val homeLineup: List<MatchPlayer> = emptyList(),
    val awayLineup: List<MatchPlayer> = emptyList(),
) {
    val matchTime: String get() = kickoffTime
    val scoreText: String get() = toScoreText()
}

/**
 * Returns kickoff time if the match hasn't started, or current score ("1 - 0") if live or finished.
 */
fun MatchDetails.toScoreText(): String {
    return if (!isLive && !isCompleted) {
        kickoffTime.ifBlank { "VS" }
    } else {
        "$homeTeamScore - $awayTeamScore"
    }
}

fun MatchDetails.toMatchScore(): String = toScoreText()

/**
 * Flat key-value comparison for a specific match statistic.
 */
data class MatchStatItem(
    val name: String,
    val label: String,
    val homeValue: String,
    val awayValue: String,
)

/**
 * Flattened timeline event (goal, card, substitution, etc.).
 */
data class MatchTimelineEvent(
    val id: String,
    val minute: String,
    val type: String,
    val isGoal: Boolean = false,
    val isRedCard: Boolean = false,
    val isYellowCard: Boolean = false,
    val isSubstitution: Boolean = false,
    val text: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val playerName: String = "",
    val assistPlayerName: String? = null,
)

/**
 * Flattened player information for starting XI or substitutes.
 */
data class MatchPlayer(
    val id: String,
    val name: String,
    val jersey: String,
    val position: String,
    val isStarter: Boolean,
    val isSubbedIn: Boolean = false,
    val isSubbedOut: Boolean = false,
)
