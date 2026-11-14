package com.zeyadmamoun.fixtures.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.fixtures.ui.MatchesScreen

fun EntryProviderScope<NavKey>.matchesEntryProvider(
    onMatchClicked: (String, String) -> Unit = {_, _ ->}
){
    entry<MatchesRoute> {
        MatchesScreen(
            onMatchClicked = onMatchClicked
        )
    }
}