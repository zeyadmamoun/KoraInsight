package com.zeyadmamoun.data.model.news

import com.zeyadmamoun.network.model.news.EspnNewsArticleDto
import kotlin.time.Instant

/**
 * Flat, UI-ready representation of a single news article.
 *
 * Intentionally holds only scalars: no nested image/link/league objects are retained, so a
 * merged feed of several hundred articles stays cheap in memory. The nested ESPN payload is
 * flattened once during mapping.
 *
 * @property leagueSlug The slug of the league the article was fetched for (e.g. `eng.1`).
 * This is taken from the request rather than parsed out of the payload, because the feed is
 * queried one league at a time.
 * @property type One of `Story`, `HeadlineNews`, `Recap` or `Media`.
 * @property espnUrl Canonical ESPN web URL, used as the tap target for video items.
 */
data class NewsArticle(
    val id: String,
    val headline: String,
    val summary: String? = null,
    val imageUrl: String? = null,
    val publishedAt: Instant? = null,
    val leagueSlug: String,
    val type: String,
    val espnUrl: String? = null,
) {
    /** `Media` articles are video clips and have no story body. */
    val isVideo: Boolean get() = type.equals("Media", ignoreCase = true)
}

/**
 * Maps an ESPN article into the flat [NewsArticle] domain model.
 *
 * @param leagueSlug The slug of the league request that produced this article.
 */
fun EspnNewsArticleDto.toNewsArticle(leagueSlug: String): NewsArticle = NewsArticle(
    id = id.toString(),
    headline = headline.orEmpty(),
    summary = description?.takeIf { it.isNotBlank() },
    imageUrl = bestImageUrl(),
    publishedAt = published?.let { runCatching { Instant.parse(it) }.getOrNull() },
    leagueSlug = leagueSlug,
    type = type.orEmpty(),
    espnUrl = links?.web?.href ?: links?.mobile?.href,
)

/**
 * Prefers the article photo (`type == "header"`) and falls back to the largest available
 * image, which is usually the video thumbnail.
 */
private fun EspnNewsArticleDto.bestImageUrl(): String? =
    images.firstOrNull { it.type == "header" && !it.url.isNullOrBlank() }?.url
        ?: images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }?.url
