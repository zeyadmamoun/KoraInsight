package com.zeyadmamoun.leagues

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.leagues.navigation.LeaguesRoute
import com.zeyadmamoun.leagues.ui.LeagueScreen

fun EntryProviderScope<NavKey>.leaguesEntryProvider(){
    entry<LeaguesRoute> {
        LeagueScreen()
    }
}