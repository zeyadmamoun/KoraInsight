package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.zeyadmamoun.fixtures.navigation.matchesEntryProvider
import com.zeyadmamoun.leagues.leaguesEntryProvider
import com.zeyadmamoun.matchdetails.navigation.matchDetailsEntryProvider
import com.zeyadmamoun.news.navigation.newsEntryProvider
import com.zeyadmamoun.profile.navigation.profileEntryProvider

@Composable
fun KoraInsightNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
){
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            matchesEntryProvider()
            leaguesEntryProvider()
            profileEntryProvider()
            newsEntryProvider()
            matchDetailsEntryProvider(
                onBackClick = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.size - 1)
                    }
                }
            )
        },
        modifier = modifier
    )
}
