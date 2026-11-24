package com.zeyadmamoun.matchdetails.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.data.repository.MatchDetailsRepository
import com.zeyadmamoun.matchdetails.ui.MatchDetailsScreen
import com.zeyadmamoun.matchdetails.ui.MatchDetailsViewModel
import javax.inject.Inject

/**
 * Navigation 3 EntryProvider for the Match Details feature.
 *
 * @param onBackClick Action to execute when the user taps back.
 */
fun EntryProviderScope<NavKey>.matchDetailsEntryProvider(
    onBackClick: () -> Unit = {}
) {
    entry<MatchDetailsRoute> { key ->
        val viewModel = hiltViewModel<MatchDetailsViewModel, MatchDetailsViewModel.Factory>(
            creationCallback = { factory -> factory.create(key.matchId,key.leagueSlug) }
        )

        MatchDetailsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}
