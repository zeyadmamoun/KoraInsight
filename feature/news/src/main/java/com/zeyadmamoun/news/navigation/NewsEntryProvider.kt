package com.zeyadmamoun.news.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.news.ui.ArticleDetailsScreen
import com.zeyadmamoun.news.ui.ArticleDetailsViewModel
import com.zeyadmamoun.news.ui.NewsScreen

/**
 * Navigation 3 EntryProvider for the News feed tab.
 */
fun EntryProviderScope<NavKey>.newsEntryProvider(
    onArticleClick: (articleId: String, leagueName: String) -> Unit = { _, _ -> },
) {
    entry<NewsRoute> {
        NewsScreen(onArticleClick = onArticleClick)
    }
}

/**
 * Navigation 3 EntryProvider for the Article Details destination.
 */
fun EntryProviderScope<NavKey>.articleDetailsEntryProvider(
    onBackClick: () -> Unit = {},
) {
    entry<ArticleDetailsRoute> { key ->
        val viewModel = hiltViewModel<ArticleDetailsViewModel, ArticleDetailsViewModel.Factory>(
            creationCallback = { factory -> factory.create(key.articleId, key.leagueName) }
        )

        ArticleDetailsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}
