package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.news.ArticleDetails
import com.zeyadmamoun.data.model.news.NewsArticle
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for football news.
 *
 * Fetches the news feed of every requested league in parallel and exposes a single merged,
 * de-duplicated and time-ordered list wrapped in a [Result] stream.
 */
interface NewsRepository {

    /**
     * Retrieves the merged news feed for the provided leagues.
     *
     * One request is issued per league, exactly mirroring the fixtures fan-out. Individual
     * league failures are isolated: as long as one league succeeds, the feed is still shown.
     *
     * Articles shared by several leagues (e.g. a transfer story tagged `eng.1` and `ger.2`)
     * are de-duplicated by id, keeping the league that appears **first** in [leagueSlugs].
     * Callers should therefore pass the list in display-priority order so that an article is
     * attributed to its most relevant chip.
     *
     * @param leagueSlugs The leagues to query.
     * @param isForceRefresh Whether to bypass the short-lived cache and force a network fetch.
     * @return A cold [Flow] emitting:
     * - [Result.Loading] while requests are in flight.
     * - [Result.Success] with the merged list sorted by publication date, newest first.
     * - [Result.Error] if the slug list is empty or every league request failed.
     */
    fun getNews(
        leagueSlugs: List<LeagueSlug>,
        isForceRefresh: Boolean
    ): Flow<Result<List<NewsArticle>>>

    /**
     * Fetches full details for a single article without caching.
     *
     * @param articleId Unique ESPN article ID.
     * @param leagueName Optional league display name carried from the feed item.
     */
    fun getArticleDetails(
        articleId: String,
        leagueName: String = "",
    ): Flow<Result<ArticleDetails>>
}
