package com.zeyadmamoun.data.repository

import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.news.ArticleDetails
import com.zeyadmamoun.data.model.news.NewsArticle
import com.zeyadmamoun.data.model.news.toArticleDetails
import com.zeyadmamoun.data.model.news.toNewsArticle
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.network.ktor.NetworkDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

/**
 * Default implementation of [NewsRepository] delegating to [NetworkDataSource].
 *
 * Mirrors the fan-out used for fixtures ([MatchRepositoryImpl]): structured concurrency with
 * [coroutineScope] and [async] issues one request per league without blocking worker threads,
 * and a failing league is absorbed so the remaining ones still reach the UI.
 *
 * The merged result is cached in memory behind a [Mutex] for [CACHE_TTL_MILLIS]. Unlike the
 * fixtures cache, which is keyed by date and never expires, news is time sensitive, so the
 * entry is invalidated by age and replaced atomically.
 *
 * @property networkDataSource The remote network client (powered by Ktor).
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : NewsRepository {

    private val cacheMutex = Mutex()

    /** Immutable snapshot of the last successful merge. */
    private var cachedArticles: List<NewsArticle>? = null

    /** Epoch millis at which [cachedArticles] was written. */
    private var cachedAtMillis: Long = 0L

    override fun getNews(
        leagueSlugs: List<LeagueSlug>,
        isForceRefresh: Boolean
    ): Flow<Result<List<NewsArticle>>> = flow {
        // 1. Guard against empty requests before emitting loading state
        if (leagueSlugs.isEmpty()) {
            emit(Result.Error("No leagues selected"))
            return@flow
        }

        emit(Result.Loading)

        // 2. Serve the cached feed while it is still fresh
        if (!isForceRefresh) {
            readCache()?.let { cached ->
                emit(Result.Success(cached))
                return@flow
            }
        }

        // 3. Fire one request per league concurrently, isolating failures per league
        val articlesByLeague = coroutineScope {
            leagueSlugs.map { league ->
                async {
                    try {
                        networkDataSource.getNews(league.slug, NEWS_LIMIT)
                            .articles
                            .map { it.toNewsArticle(league.slug) }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        e.printStackTrace()
                        null // Absorbs failure so other leagues continue uninterrupted
                    }
                }
            }.awaitAll().filterNotNull()
        }

        // 4. Merge, keep one entry per article, then order newest first.
        //    distinctBy runs on the league-ordered list, so the earliest league wins the slug.
        val merged = articlesByLeague
            .flatten()
            .distinctBy { it.id }
            .sortedWith(
                compareByDescending<NewsArticle> { it.publishedAt }
                    .thenByDescending { it.id }
            )

        // 5. Emit success with partial or full results, or error if all failed
        if (merged.isEmpty()) {
            emit(Result.Error("Failed to fetch news for all leagues"))
        } else {
            writeCache(merged)
            emit(Result.Success(merged))
        }
    }.flowOn(Dispatchers.IO)

    override fun getArticleDetails(
        articleId: String,
        leagueName: String,
    ): Flow<Result<ArticleDetails>> = flow {
        if (articleId.isBlank()) {
            emit(Result.Error("Invalid article ID"))
            return@flow
        }

        emit(Result.Loading)

        try {
            val detailDto = networkDataSource.getArticleDetails(articleId).headlines.firstOrNull()
            if (detailDto == null) {
                emit(Result.Error("Article not found"))
            } else {
                emit(Result.Success(detailDto.toArticleDetails(fallbackLeagueName = leagueName)))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emit(Result.Error(e.message ?: "Failed to load article"))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun readCache(): List<NewsArticle>? = cacheMutex.withLock {
        val cached = cachedArticles ?: return@withLock null
        val ageMillis = Clock.System.now().toEpochMilliseconds() - cachedAtMillis
        if (ageMillis in 0 until CACHE_TTL_MILLIS) cached else null
    }

    private suspend fun writeCache(articles: List<NewsArticle>) = cacheMutex.withLock {
        cachedArticles = articles
        cachedAtMillis = Clock.System.now().toEpochMilliseconds()
    }

    private companion object {
        /** ESPN caps the news feed at 50 articles per league. */
        const val NEWS_LIMIT = 20

        /** How long a merged feed is served before a network fetch is required again. */
        const val CACHE_TTL_MILLIS = 10 * 60 * 1000L
    }
}
