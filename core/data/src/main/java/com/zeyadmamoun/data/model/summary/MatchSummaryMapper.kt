package com.zeyadmamoun.data.model.summary

import com.zeyadmamoun.core.common.datetime.toLocalTimeString
import com.zeyadmamoun.network.model.summary.EspnMatchSummaryResponseDto
import com.zeyadmamoun.network.model.summary.EspnSummaryBoxscoreTeamDto
import com.zeyadmamoun.network.model.summary.EspnSummaryKeyEventDto
import com.zeyadmamoun.network.model.summary.EspnSummaryPlayerDto

/**
 * Maps the raw ESPN summary response DTO into a flattened [MatchDetails] domain model.
 */
fun EspnMatchSummaryResponseDto.toMatchDetails(): MatchDetails {
    val competition = header?.competitions?.firstOrNull()

    // 1. Identify Home & Away Competitors
    val competitors = competition?.competitors.orEmpty()
    val homeCompetitor = competitors.firstOrNull { it.homeAway.equals("home", ignoreCase = true) }
        ?: competitors.getOrNull(0)
    val awayCompetitor = competitors.firstOrNull { it.homeAway.equals("away", ignoreCase = true) }
        ?: competitors.getOrNull(1)

    // 2. Identify Home & Away Boxscore Teams
    val boxscoreTeams = boxscore?.teams.orEmpty()
    val homeBoxscore = boxscoreTeams.firstOrNull { it.homeAway.equals("home", ignoreCase = true) }
        ?: boxscoreTeams.getOrNull(0)
    val awayBoxscore = boxscoreTeams.firstOrNull { it.homeAway.equals("away", ignoreCase = true) }
        ?: boxscoreTeams.getOrNull(1)

    val homeTeamId = homeCompetitor?.team?.id ?: homeBoxscore?.team?.id.orEmpty()
    val awayTeamId = awayCompetitor?.team?.id ?: awayBoxscore?.team?.id.orEmpty()

    // 3. Extract Statistics
    val homePossessionRaw = homeBoxscore.findStat("possessionPct")
    val awayPossessionRaw = awayBoxscore.findStat("possessionPct")
    val homePossession = if (homePossessionRaw.isNotBlank()) "$homePossessionRaw%" else "50%"
    val awayPossession = if (awayPossessionRaw.isNotBlank()) "$awayPossessionRaw%" else "50%"

    val homePassAccRaw = homeBoxscore.findStat("passPct")
    val awayPassAccRaw = awayBoxscore.findStat("passPct")
    val homePassAccuracy = if (homePassAccRaw.isNotBlank()) "$homePassAccRaw%" else "0%"
    val awayPassAccuracy = if (awayPassAccRaw.isNotBlank()) "$awayPassAccRaw%" else "0%"

    // Combine comparative statistics into a list
    val allStats = mutableListOf<MatchStatItem>()
    val statNames = listOf(
        "possessionPct" to "Possession",
        "totalShots" to "Total Shots",
        "shotsOnTarget" to "Shots on Target",
        "foulsCommitted" to "Fouls Committed",
        "yellowCards" to "Yellow Cards",
        "redCards" to "Red Cards",
        "wonCorners" to "Corner Kicks",
        "saves" to "Goalkeeper Saves",
        "totalPasses" to "Total Passes",
        "accuratePasses" to "Accurate Passes",
        "offsides" to "Offsides"
    )
    statNames.forEach { (key, label) ->
        val homeVal = homeBoxscore.findStat(key)
        val awayVal = awayBoxscore.findStat(key)
        if (homeVal.isNotBlank() || awayVal.isNotBlank()) {
            allStats.add(
                MatchStatItem(
                    name = key,
                    label = label,
                    homeValue = homeVal.ifBlank { "0" },
                    awayValue = awayVal.ifBlank { "0" }
                )
            )
        }
    }

    // 4. Rosters & Formations
    val homeRoster = rosters.firstOrNull { it.team?.id == homeTeamId } ?: rosters.getOrNull(0)
    val awayRoster = rosters.firstOrNull { it.team?.id == awayTeamId } ?: rosters.getOrNull(1)

    // 5. Timeline Events
    val mappedEvents = keyEvents.mapNotNull { it.toMatchTimelineEvent() }

    // 6. Live Status & Current Minute
    val status = competition?.status
    val statusType = status?.type
    val isLive = statusType?.state?.trim().equals("in", ignoreCase = true)
    val currentMinute = extractLiveMinute(
        isLive = isLive,
        displayClock = status?.displayClock,
        detail = statusType?.detail,
        shortDetail = statusType?.shortDetail
    )

    val rawDate = competition?.date.orEmpty()
    val kickoffTime = if (rawDate.isNotBlank()) {
        rawDate.toLocalTimeString().takeIf { it != "VS" }.orEmpty()
    } else {
        ""
    }

    return MatchDetails(
        id = header?.id ?: competition?.id.orEmpty(),
        date = rawDate,
        kickoffTime = kickoffTime,
        leagueName = header?.league?.name.orEmpty(),
        leagueSlug = header?.league?.slug.orEmpty(),
        statusDescription = statusType?.description ?: "Scheduled",
        statusDetail = statusType?.detail ?: statusType?.shortDetail.orEmpty(),
        isLive = isLive,
        currentMinute = currentMinute,
        isCompleted = statusType?.completed ?: false,

        // Home Team
        homeTeamId = homeTeamId,
        homeTeamName = homeCompetitor?.team?.name ?: homeBoxscore?.team?.name.orEmpty(),
        homeTeamDisplayName = homeCompetitor?.team?.displayName ?: homeBoxscore?.team?.displayName.orEmpty(),
        homeTeamAbbreviation = homeCompetitor?.team?.abbreviation ?: homeBoxscore?.team?.abbreviation.orEmpty(),
        homeTeamLogo = homeCompetitor?.team?.logos?.firstOrNull()?.href
            ?: homeBoxscore?.team?.logo.orEmpty(),
        homeTeamColor = homeBoxscore?.team?.color.orEmpty(),
        homeTeamScore = homeCompetitor?.score ?: "0",
        isHomeWinner = homeCompetitor?.winner ?: false,

        // Away Team
        awayTeamId = awayTeamId,
        awayTeamName = awayCompetitor?.team?.name ?: awayBoxscore?.team?.name.orEmpty(),
        awayTeamDisplayName = awayCompetitor?.team?.displayName ?: awayBoxscore?.team?.displayName.orEmpty(),
        awayTeamAbbreviation = awayCompetitor?.team?.abbreviation ?: awayBoxscore?.team?.abbreviation.orEmpty(),
        awayTeamLogo = awayCompetitor?.team?.logos?.firstOrNull()?.href
            ?: awayBoxscore?.team?.logo.orEmpty(),
        awayTeamColor = awayBoxscore?.team?.color.orEmpty(),
        awayTeamScore = awayCompetitor?.score ?: "0",
        isAwayWinner = awayCompetitor?.winner ?: false,

        // Venue
        venueName = gameInfo?.venue?.fullName.orEmpty(),
        venueCity = listOfNotNull(
            gameInfo?.venue?.address?.city,
            gameInfo?.venue?.address?.country
        ).filter { it.isNotBlank() }.joinToString(", "),
        attendance = gameInfo?.attendance,

        // Key Stats
        homePossession = homePossession,
        awayPossession = awayPossession,
        homeTotalShots = homeBoxscore.findStat("totalShots").ifBlank { "0" },
        awayTotalShots = awayBoxscore.findStat("totalShots").ifBlank { "0" },
        homeShotsOnTarget = homeBoxscore.findStat("shotsOnTarget").ifBlank { "0" },
        awayShotsOnTarget = awayBoxscore.findStat("shotsOnTarget").ifBlank { "0" },
        homeFouls = homeBoxscore.findStat("foulsCommitted").ifBlank { "0" },
        awayFouls = awayBoxscore.findStat("foulsCommitted").ifBlank { "0" },
        homeYellowCards = homeBoxscore.findStat("yellowCards").ifBlank { "0" },
        awayYellowCards = awayBoxscore.findStat("yellowCards").ifBlank { "0" },
        homeRedCards = homeBoxscore.findStat("redCards").ifBlank { "0" },
        awayRedCards = awayBoxscore.findStat("redCards").ifBlank { "0" },
        homeCorners = homeBoxscore.findStat("wonCorners").ifBlank { "0" },
        awayCorners = awayBoxscore.findStat("wonCorners").ifBlank { "0" },
        homeSaves = homeBoxscore.findStat("saves").ifBlank { "0" },
        awaySaves = awayBoxscore.findStat("saves").ifBlank { "0" },
        homePasses = homeBoxscore.findStat("totalPasses").ifBlank { "0" },
        awayPasses = awayBoxscore.findStat("totalPasses").ifBlank { "0" },
        homePassAccuracy = homePassAccuracy,
        awayPassAccuracy = awayPassAccuracy,

        // Formations
        homeFormation = homeRoster?.formation,
        awayFormation = awayRoster?.formation,

        // Lists
        allStats = allStats,
        events = mappedEvents,
        homeLineup = homeRoster?.roster?.map { it.toMatchPlayer() }.orEmpty(),
        awayLineup = awayRoster?.roster?.map { it.toMatchPlayer() }.orEmpty()
    )
}

/**
 * Safely looks up a statistic displayValue by name.
 */
private fun EspnSummaryBoxscoreTeamDto?.findStat(statName: String): String {
    return this?.statistics?.firstOrNull { it.name.equals(statName, ignoreCase = true) }?.displayValue.orEmpty()
}

/**
 * Extracts the current broadcast minute when the match is live, or empty string otherwise.
 */
private fun extractLiveMinute(
    isLive: Boolean,
    displayClock: String?,
    detail: String?,
    shortDetail: String?,
): String {
    if (!isLive) return ""

    val trimmedDetail = detail?.trim()
    val trimmedShortDetail = shortDetail?.trim()
    val trimmedClock = displayClock?.trim()

    return when {
        trimmedDetail.equals("HT", ignoreCase = true) ||
            trimmedShortDetail.equals("HT", ignoreCase = true) -> "HT"
        !trimmedClock.isNullOrBlank() -> trimmedClock
        !trimmedDetail.isNullOrBlank() -> trimmedDetail
        !trimmedShortDetail.isNullOrBlank() -> trimmedShortDetail
        else -> ""
    }
}

/**
 * Maps an ESPN key event DTO into a flat [MatchTimelineEvent].
 */
fun EspnSummaryKeyEventDto.toMatchTimelineEvent(): MatchTimelineEvent? {
    val eventType = type?.text ?: type?.type ?: return null
    val typeLower = eventType.lowercase()
    val rawText = (text ?: shortText.orEmpty()).lowercase()

    val isPenalty = typeLower.contains("penalty") || rawText.contains("penalty")
    val isYellowRed = typeLower.contains("second yellow") ||
        typeLower.contains("yellow-red") ||
        typeLower.contains("yellow card / red card") ||
        rawText.contains("second yellow") ||
        rawText.contains("yellow card / red card")
    val isGoal = (scoringPlay || typeLower.contains("goal")) && !isYellowRed
    val isRed = (typeLower.contains("red") || rawText.contains("red card")) && !isYellowRed
    val isYellow = (typeLower.contains("yellow") || rawText.contains("yellow card")) && !isYellowRed
    val isSub = typeLower.contains("substitution") || rawText.contains("substitution")

    val mainAthlete = participants.firstOrNull()?.athlete?.displayName.orEmpty()
    val secondaryAthlete = participants.getOrNull(1)?.athlete?.displayName

    return MatchTimelineEvent(
        id = id.orEmpty(),
        minute = clock?.displayValue.orEmpty(),
        type = eventType,
        isGoal = isGoal,
        isPenalty = isPenalty,
        isRedCard = isRed,
        isYellowCard = isYellow,
        isYellowRedCard = isYellowRed,
        isSubstitution = isSub,
        text = text ?: shortText.orEmpty(),
        teamId = team?.id.orEmpty(),
        teamName = team?.displayName.orEmpty(),
        playerName = mainAthlete,
        assistPlayerName = if (!isSub) secondaryAthlete else null,
        subPlayerIn = if (isSub) mainAthlete else null,
        subPlayerOut = if (isSub) secondaryAthlete else null
    )
}

/**
 * Maps an ESPN roster player into a flat [MatchPlayer].
 */
fun EspnSummaryPlayerDto.toMatchPlayer(): MatchPlayer {
    return MatchPlayer(
        id = athlete?.id.orEmpty(),
        name = athlete?.displayName.orEmpty(),
        jersey = jersey ?: athlete?.jersey.orEmpty(),
        position = position?.name ?: position?.abbreviation.orEmpty(),
        isStarter = starter,
        isSubbedIn = subbedIn,
        isSubbedOut = subbedOut
    )
}
