package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.zeyadmamoun.fixtures.navigation.matchesEntryProvider
import com.zeyadmamoun.leagues.leaguesEntryProvider
import com.zeyadmamoun.matchdetails.navigation.MatchDetailsRoute
import com.zeyadmamoun.matchdetails.navigation.matchDetailsEntryProvider
import com.zeyadmamoun.news.navigation.ArticleDetailsRoute
import com.zeyadmamoun.news.navigation.articleDetailsEntryProvider
import com.zeyadmamoun.news.navigation.newsEntryProvider
import com.zeyadmamoun.profile.navigation.profileEntryProvider

@Composable
fun KoraInsightNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
){
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            matchesEntryProvider(
                onMatchClicked = { matchId, leagueSlug ->
                    backStack.add(
                        MatchDetailsRoute(matchId = matchId, leagueSlug = leagueSlug)
                    )
                }
            )
            leaguesEntryProvider()
            profileEntryProvider()
            newsEntryProvider(
                onArticleClick = { articleId, leagueName ->
                    backStack.add(
                        ArticleDetailsRoute(articleId = articleId, leagueName = leagueName)
                    )
                }
            )
            articleDetailsEntryProvider(
                onBackClick = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.size - 1)
                    }
                }
            )
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
