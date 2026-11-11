package com.zeyadmamoun.matchdetails.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.matchdetails.ui.MatchDetailsScreen

/**
 * Navigation 3 EntryProvider for the Match Details feature.
 *
 * @param onBackClick Action to execute when the user taps back.
 */
fun EntryProviderScope<NavKey>.matchDetailsEntryProvider(
    onBackClick: () -> Unit = {}
) {
    entry<MatchDetailsRoute> { key ->
        MatchDetailsScreen(
            matchId = key.matchId,
            leagueSlug = key.leagueSlug,
            onBackClick = onBackClick
        )
    }
}
