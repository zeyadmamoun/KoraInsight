package com.zeyadmamoun.matchdetails.ui

import com.zeyadmamoun.data.model.summary.MatchDetails

/**
 * Builds the complete system instruction prompt from [MatchDetails] for the AI tactical analyst,
 * including all key timeline events (goals, cards, substitutions).
 */
fun MatchDetails.toTacticalSystemPrompt(): String {
    val timelineText = if (events.isEmpty()) {
        "No key timeline events recorded."
    } else {
        events
            .filter { !it.isPeriodDivider }
            .joinToString("\n") { event ->
                val details = when {
                    event.isGoal && !event.assistPlayerName.isNullOrBlank() ->
                        "${event.playerName} (Assist: ${event.assistPlayerName})"
                    event.isSubstitution && !event.subPlayerIn.isNullOrBlank() ->
                        "In: ${event.subPlayerIn}, Out: ${event.subPlayerOut ?: "N/A"}"
                    event.playerName.isNotBlank() -> event.playerName
                    else -> event.text.ifBlank { event.type }
                }
                "- ${event.minute}': ${event.type} (${event.teamName}) - $details"
            }
    }

    return """
        You are an elite football tactical analyst for KoraInsight.
        Answer user questions strictly using ONLY this match data.

        === MATCH OVERVIEW ===
        Competition: $leagueName
        Venue: $venueName
        Score: $homeTeamName $homeTeamScore - $awayTeamScore $awayTeamName
        Formations: ${homeFormation ?: "N/A"} vs ${awayFormation ?: "N/A"}

        === TEAM STATISTICS ===
        - Possession: $homePossession vs $awayPossession
        - Total Shots (On Target): $homeTotalShots ($homeShotsOnTarget) vs $awayTotalShots ($awayShotsOnTarget)
        - Pass Accuracy: $homePassAccuracy vs $awayPassAccuracy
        - Corners: $homeCorners vs $awayCorners
        - Saves: $homeSaves vs $awaySaves
        - Fouls: $homeFouls vs $awayFouls

        === KEY MATCH TIMELINE ===
        $timelineText
    """.trimIndent()
}

/**
 * Appends a specific user question directly to the full match context.
 */
fun MatchDetails.toTacticalPromptWithQuestion(userQuestion: String): String {
    return """
        ${toTacticalSystemPrompt()}

        === USER QUESTION ===
        $userQuestion
    """.trimIndent()
}
