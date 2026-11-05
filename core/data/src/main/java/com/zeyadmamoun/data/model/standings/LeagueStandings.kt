package com.zeyadmamoun.data.model.standings

import com.zeyadmamoun.network.model.standings.EspnStandingsResponseDto
import com.zeyadmamoun.network.model.standings.StandingsEntryDto

data class LeagueStandings(
    val leagueId: String,
    val leagueUid: String? = null,
    val leagueName: String,
    val leagueAbbreviation: String?,
    val seasonYear: Int?,
    val seasonDisplayName: String?,
    val currentRound: Int,
    val groups: List<StandingsGroup> = emptyList(),
) {
    /**
     * Direct list of table entries across groups, or the single table for standard domestic leagues.
     */
    val entries: List<LeagueTableEntry> get() = groups.flatMap { it.entries }
}

data class StandingsGroup(
    val id: String?,
    val name: String,
    val abbreviation: String?,
    val entries: List<LeagueTableEntry> = emptyList(),
)

data class LeagueTableEntry(
    val teamId: String,
    val teamName: String,
    val teamDisplayName: String,
    val teamAbbreviation: String,
    val teamLogoUrl: String?,
    val rank: Int,
    val gamesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,
    val overallRecord: String? = null,
    val qualificationNote: String? = null,
    val qualificationColor: String? = null,
    val qualificationRank: Int? = null,
)

fun EspnStandingsResponseDto.toLeagueStandings(): LeagueStandings {
    val mappedGroups = children.map { child ->
        val childEntries = child.standings?.entries?.map { it.toLeagueTableEntry() }.orEmpty()
        StandingsGroup(
            id = child.id,
            name = child.name ?: name.orEmpty(),
            abbreviation = child.abbreviation ?: abbreviation,
            entries = childEntries,
        )
    }

    val allEntries = mappedGroups.flatMap { it.entries }
    val maxRound = allEntries.maxOfOrNull { it.gamesPlayed } ?: 0
    val firstStandings = children.firstOrNull()?.standings

    return LeagueStandings(
        leagueId = id ?: "",
        leagueUid = uid ?: children.firstOrNull()?.uid,
        leagueName = name ?: children.firstOrNull()?.name.orEmpty(),
        leagueAbbreviation = abbreviation ?: children.firstOrNull()?.abbreviation,
        seasonYear = firstStandings?.season ?: season?.year,
        seasonDisplayName = firstStandings?.seasonDisplayName ?: season?.displayName,
        currentRound = maxRound,
        groups = mappedGroups,
    )
}

fun StandingsEntryDto.toLeagueTableEntry(): LeagueTableEntry {
    fun findStatValue(vararg names: String): Int {
        for (name in names) {
            val stat = stats.firstOrNull { it.name.equals(name, ignoreCase = true) }
            val v = stat?.value
            if (v != null) return v.toInt()
        }
        return 0
    }

    fun findStatDisplay(name: String): String? {
        return stats.firstOrNull { it.name.equals(name, ignoreCase = true) }?.displayValue
    }

    val rankValue = findStatValue("rank").takeIf { it > 0 } ?: note?.rank ?: 0

    return LeagueTableEntry(
        teamId = team.id,
        teamName = team.name.orEmpty(),
        teamDisplayName = team.displayName ?: team.name.orEmpty(),
        teamAbbreviation = team.abbreviation.orEmpty(),
        teamLogoUrl = team.logos.firstOrNull()?.href,
        rank = rankValue,
        gamesPlayed = findStatValue("gamesPlayed"),
        wins = findStatValue("wins"),
        draws = findStatValue("ties", "draws"),
        losses = findStatValue("losses"),
        goalsFor = findStatValue("pointsFor", "goalsFor"),
        goalsAgainst = findStatValue("pointsAgainst", "goalsAgainst"),
        goalDifference = findStatValue("pointDifferential", "goalDifference"),
        points = findStatValue("points"),
        overallRecord = findStatDisplay("overall"),
        qualificationNote = note?.description,
        qualificationColor = note?.color,
        qualificationRank = note?.rank,
    )
}
