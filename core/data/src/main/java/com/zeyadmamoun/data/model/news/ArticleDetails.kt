package com.zeyadmamoun.data.model.news

import com.zeyadmamoun.network.model.news.EspnArticleDetailDto
import kotlin.time.Instant

/**
 * Full editorial or media article details mapped from ESPN's article detail endpoint.
 */
data class ArticleDetails(
    val id: String,
    val headline: String,
    val description: String? = null,
    val leagueName: String = "",
    val byline: String? = null,
    val publishedAt: Instant? = null,
    val lastModifiedAt: Instant? = null,
    val isVideo: Boolean = false,
    val heroImageUrl: String? = null,
    val heroCaption: String? = null,
    val heroCredit: String? = null,
    val videoUrl: String? = null,
    val videoDurationSeconds: Int? = null,
    val topics: List<String> = emptyList(),
    val storyParagraphs: List<String> = emptyList(),
    val espnUrl: String? = null,
)

/**
 * Maps an [EspnArticleDetailDto] into the [ArticleDetails] domain model.
 *
 * @param fallbackLeagueName Display name passed from the feed chip/item when the payload
 * omits or uses a generic `section` (e.g. `"Soccer"`).
 */
fun EspnArticleDetailDto.toArticleDetails(fallbackLeagueName: String = ""): ArticleDetails {
    val primaryImage = images.firstOrNull { it.type == "header" && !it.url.isNullOrBlank() }
        ?: images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }
    val primaryVideo = video.firstOrNull()
    val resolvedVideoUrl = primaryVideo?.links?.source?.let { source ->
        source.hd?.href ?: source.href ?: source.hls?.href
    } ?: primaryVideo?.links?.web?.href

    val resolvedLeague = fallbackLeagueName.ifBlank {
        categories.firstOrNull { it.type.equals("league", ignoreCase = true) && !it.description.equals("Soccer", ignoreCase = true) }
            ?.description
            ?: section.orEmpty()
    }

    val topicTags = categories
        .filter { category ->
            val type = category.type.orEmpty()
            val desc = category.description.orEmpty()
            (type.equals("team", ignoreCase = true) || type.equals("league", ignoreCase = true)) &&
                desc.isNotBlank() &&
                !desc.equals("Soccer", ignoreCase = true)
        }
        .mapNotNull { it.description?.trim() }
        .distinct()
        .take(6)

    return ArticleDetails(
        id = id.toString(),
        headline = headline.orEmpty(),
        description = description?.takeIf { it.isNotBlank() },
        leagueName = resolvedLeague,
        byline = byline?.takeIf { it.isNotBlank() },
        publishedAt = published?.let { runCatching { Instant.parse(it) }.getOrNull() },
        lastModifiedAt = lastModified?.let { runCatching { Instant.parse(it) }.getOrNull() },
        isVideo = type.equals("Media", ignoreCase = true) || !resolvedVideoUrl.isNullOrBlank(),
        heroImageUrl = primaryImage?.url ?: primaryVideo?.thumbnail,
        heroCaption = primaryImage?.caption?.takeIf { it.isNotBlank() },
        heroCredit = primaryImage?.credit?.takeIf { it.isNotBlank() },
        videoUrl = resolvedVideoUrl,
        videoDurationSeconds = primaryVideo?.duration,
        topics = topicTags,
        storyParagraphs = parseStoryParagraphs(story),
        espnUrl = links?.web?.href ?: links?.mobile?.href ?: primaryVideo?.links?.web?.href,
    )
}

private val HTML_TAG_REGEX = Regex("<[^>]+>")
private val PARAGRAPH_SPLIT_REGEX = Regex("</p>|<br\\s*/?>", RegexOption.IGNORE_CASE)
private val PLACEHOLDER_TAG_REGEX = Regex("<(alsosee|photo\\d+|video\\d+|inline\\d*)\\b[^>]*>(.*?)</\\1>|<(alsosee|photo\\d+|video\\d+|inline\\d*)\\s*/?>", RegexOption.IGNORE_CASE)
private val WHITESPACE_REGEX = Regex("\\s+")

private fun parseStoryParagraphs(rawHtml: String?): List<String> {
    if (rawHtml.isNullOrBlank()) return emptyList()
    val withoutEmbeds = PLACEHOLDER_TAG_REGEX.replace(rawHtml, "")
    return withoutEmbeds
        .split(PARAGRAPH_SPLIT_REGEX)
        .map { block ->
            HTML_TAG_REGEX.replace(block, "")
                .decodeBasicHtmlEntities()
                .replace(WHITESPACE_REGEX, " ")
                .trim()
        }
        .filter { it.isNotBlank() }
}

private fun String.decodeBasicHtmlEntities(): String = this
    .replace("&amp;", "&")
    .replace("&quot;", "\"")
    .replace("&apos;", "'")
    .replace("&#39;", "'")
    .replace("&#x27;", "'")
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .replace("&nbsp;", " ")
    .replace("&mdash;", "—")
    .replace("&ndash;", "–")
    .replace("&#8217;", "’")
    .replace("&#8216;", "‘")
    .replace("&#8220;", "“")
    .replace("&#8221;", "”")
