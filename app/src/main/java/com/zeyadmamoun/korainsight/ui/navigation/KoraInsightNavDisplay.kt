package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zeyadmamoun.fixtures.navigation.matchesEntryProvider
import com.zeyadmamoun.leagues.leaguesEntryProvider
import com.zeyadmamoun.profile.navigation.profileEntryProvider

@Composable
fun KoraInsightNavDisplay(
    backStack: NavBackStack<NavKey>
){
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            matchesEntryProvider()
            leaguesEntryProvider()
            profileEntryProvider()
        }
    )
}