package com.zeyadmamoun.news.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation 3 destination key for the Article Details screen.
 *
 * @property articleId Unique ESPN article ID.
 * @property leagueName Human-readable league name carried from the feed.
 */
@Serializable
data class ArticleDetailsRoute(
    val articleId: String = "",
    val leagueName: String = "",
) : NavKey
