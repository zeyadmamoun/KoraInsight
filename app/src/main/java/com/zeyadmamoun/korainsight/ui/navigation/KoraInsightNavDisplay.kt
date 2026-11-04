package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zeyadmamoun.fixtures.navigation.matchesEntryProvider
import com.zeyadmamoun.leagues.leaguesEntryProvider
import com.zeyadmamoun.profile.navigation.profileEntryProvider

@Composable
fun KoraInsightNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier.fillMaxSize()
){
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            matchesEntryProvider()
            leaguesEntryProvider()
            profileEntryProvider()
        },
        modifier = modifier
    )
}
