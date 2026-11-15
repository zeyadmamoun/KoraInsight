package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.zeyadmamoun.fixtures.navigation.MatchesRoute
import com.zeyadmamoun.korainsight.ui.navigation.bottomBar.defaultBottomNavItems

@Composable
fun rememberKoraAppState(
    backStack: NavBackStack<NavKey> = rememberNavBackStack(MatchesRoute)
): KoraAppState {
    return remember(backStack) { KoraAppState(backStack) }
}
class KoraAppState(
    val backStack: NavBackStack<NavKey>
) {
    val currentRoute: NavKey?
        get() = backStack.lastOrNull()

    val selectedBottomBarIndex: Int
        get() = defaultBottomNavItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    val isTopLevelRoute: Boolean
        get() = defaultBottomNavItems.any { it.route == currentRoute }

    fun navigateToTopLevelTab(route: NavKey) {
        if (currentRoute != route) {
            backStack.clear()
            backStack.add(route)
        }
    }
}