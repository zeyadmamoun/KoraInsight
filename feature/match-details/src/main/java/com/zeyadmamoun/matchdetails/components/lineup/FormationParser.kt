package com.zeyadmamoun.matchdetails.components.lineup

import com.zeyadmamoun.data.model.summary.MatchPlayer

/**
 * Represents a starter positioned on the tactical pitch with normalized coordinates.
 *
 * @param player The domain player model.
 * @param xRatio Normalized horizontal position (0.0f = left sideline, 1.0f = right sideline).
 * @param yRatio Normalized vertical position (0.0f = top goal line, 1.0f = bottom goal line).
 * @param isGoalkeeper Whether this player is the goalkeeper (used for kit color differentiation).
 * @param isHome Whether the player belongs to the home team.
 */
data class PositionedPlayer(
    val player: MatchPlayer,
    val xRatio: Float,
    val yRatio: Float,
    val isGoalkeeper: Boolean,
    val isHome: Boolean
)

private enum class TacticalCategory {
    GOALKEEPER,
    DEFENDER,
    DEFENSIVE_MIDFIELD,
    CENTRAL_MIDFIELD,
    ATTACKING_MIDFIELD,
    FORWARD
}

/**
 * Extracts and positions starting players onto a tactical pitch coordinate system.
 *
 * Parses formation strings like "4-2-3-1", "4-3-3", "3-5-2", ensuring each player
 * appears exactly once without duplication, and spreads players across the full width
 * of the pitch with ample horizontal breathing room.
 *
 * @param starters List of starting players for the team (isStarter == true).
 * @param formationString Formation string from the API (e.g. "4-2-3-1").
 * @param isHome If true, positions on the bottom half attacking upward; if false, top half attacking downward.
 * @return List of [PositionedPlayer] with computed (xRatio, yRatio) coordinates.
 */
fun parseAndPositionStarters(
    starters: List<MatchPlayer>,
    formationString: String?,
    isHome: Boolean
): List<PositionedPlayer> {
    if (starters.isEmpty()) return emptyList()

    // 1. Ensure unique players only (prevents accidental duplication in API data)
    val uniqueStarters = starters.distinctBy { it.id.ifBlank { it.name } }

    // 2. Classify each player into exactly ONE tactical category
    val classified = uniqueStarters.map { player ->
        player to classifyPlayer(player.position)
    }

    val gks = classified.filter { it.second == TacticalCategory.GOALKEEPER }.map { it.first }
    val actualGks = if (gks.isNotEmpty()) {
        listOf(gks.first())
    } else {
        listOf(uniqueStarters.first())
    }

    val outfieldWithCategory = classified
        .filterNot { it.first in actualGks }
        .sortedBy { it.second.ordinal } // Sort DEF -> DM -> CM -> AM -> FWD

    val sortedOutfield = outfieldWithCategory.map { it.first }

    // 3. Parse Outfield Formation Lines (e.g. "4-2-3-1" -> [4, 2, 3, 1])
    val parsedLines = formationString
        ?.split("-", " ")
        ?.mapNotNull { it.trim().toIntOrNull() }
        ?.filter { it > 0 }
        .orEmpty()

    val outfieldLineCounts: List<Int> = if (parsedLines.isNotEmpty() && parsedLines.sum() == sortedOutfield.size) {
        parsedLines
    } else {
        // Fallback: Group naturally by position categories
        val defs = outfieldWithCategory.filter { it.second == TacticalCategory.DEFENDER }
        val mids = outfieldWithCategory.filter {
            it.second in listOf(
                TacticalCategory.DEFENSIVE_MIDFIELD,
                TacticalCategory.CENTRAL_MIDFIELD,
                TacticalCategory.ATTACKING_MIDFIELD
            )
        }
        val fws = outfieldWithCategory.filter { it.second == TacticalCategory.FORWARD }

        if (defs.isNotEmpty() || mids.isNotEmpty() || fws.isNotEmpty()) {
            val counts = mutableListOf<Int>()
            if (defs.isNotEmpty()) counts.add(defs.size)
            if (mids.isNotEmpty()) counts.add(mids.size)
            if (fws.isNotEmpty()) counts.add(fws.size)
            counts
        } else {
            when (sortedOutfield.size) {
                10 -> listOf(4, 3, 3)
                9 -> listOf(4, 3, 2)
                else -> listOf(sortedOutfield.size.coerceAtLeast(1))
            }
        }
    }

    // 4. Chunk Outfield Players into Tactical Rows
    val outfieldRows = mutableListOf<List<MatchPlayer>>()
    var startIndex = 0
    for (count in outfieldLineCounts) {
        val endIndex = (startIndex + count).coerceAtMost(sortedOutfield.size)
        if (startIndex < sortedOutfield.size) {
            outfieldRows.add(sortedOutfield.subList(startIndex, endIndex))
            startIndex = endIndex
        }
    }
    if (startIndex < sortedOutfield.size && outfieldRows.isNotEmpty()) {
        val lastRow = outfieldRows.removeLast()
        outfieldRows.add(lastRow + sortedOutfield.subList(startIndex, sortedOutfield.size))
    }

    val positionedPlayers = mutableListOf<PositionedPlayer>()

    // 5. Position Goalkeeper
    val gkY = if (isHome) 0.945f else 0.055f
    actualGks.forEachIndexed { i, gk ->
        val gkX = calculateHorizontalPosition(i, actualGks.size)
        positionedPlayers.add(
            PositionedPlayer(
                player = gk,
                xRatio = gkX,
                yRatio = gkY,
                isGoalkeeper = true,
                isHome = isHome
            )
        )
    }

    // 6. Position Outfield Tactical Rows with clean spacing
    val numOutfieldRows = outfieldRows.size
    outfieldRows.forEachIndexed { rowIndex, rowPlayers ->
        val yProgression = if (numOutfieldRows <= 1) {
            0.5f
        } else {
            rowIndex.toFloat() / (numOutfieldRows - 1)
        }

        // Home: rows go upward from defenders (0.855f) towards attack (0.535f)
        // Away: rows go downward from defenders (0.145f) towards attack (0.465f)
        val yRatio = if (isHome) {
            0.855f - (yProgression * (0.855f - 0.535f))
        } else {
            0.145f + (yProgression * (0.465f - 0.145f))
        }

        rowPlayers.forEachIndexed { playerIndex, player ->
            val xRatio = calculateHorizontalPosition(playerIndex, rowPlayers.size)
            positionedPlayers.add(
                PositionedPlayer(
                    player = player,
                    xRatio = xRatio,
                    yRatio = yRatio,
                    isGoalkeeper = false,
                    isHome = isHome
                )
            )
        }
    }

    return positionedPlayers
}

/**
 * Calculates responsive horizontal placement for a player within a row.
 * Spreads players widely across the full width of the football pitch,
 * giving maximum space between adjacent players and avoiding label collisions.
 */
fun calculateHorizontalPosition(index: Int, countInRow: Int): Float {
    if (countInRow <= 1) return 0.50f

    val (xMin, xMax) = when (countInRow) {
        2 -> 0.28f to 0.72f
        3 -> 0.16f to 0.84f
        4 -> 0.09f to 0.91f
        else -> 0.07f to 0.93f
    }

    val step = (xMax - xMin) / (countInRow - 1)
    return xMin + (index * step)
}

/**
 * Maps a position string strictly to one [TacticalCategory].
 */
private fun classifyPlayer(pos: String): TacticalCategory {
    val p = pos.trim().lowercase()
    return when {
        p.startsWith("g") || p.contains("keeper") || p == "gk" ->
            TacticalCategory.GOALKEEPER

        p.startsWith("d") || p.contains("back") || p.contains("def") || p == "cb" || p == "lb" || p == "rb" ->
            TacticalCategory.DEFENDER

        p.contains("striker") || p.contains("winger") || p.startsWith("f") || p.contains("cf") || p == "st" || p == "lw" || p == "rw" ->
            TacticalCategory.FORWARD

        p.contains("att") || p.contains("am") ->
            TacticalCategory.ATTACKING_MIDFIELD

        p.contains("def") || p.contains("dm") ->
            TacticalCategory.DEFENSIVE_MIDFIELD

        p.startsWith("m") || p.contains("mid") || p == "cm" || p == "mf" ->
            TacticalCategory.CENTRAL_MIDFIELD

        else ->
            TacticalCategory.FORWARD
    }
}
