package com.zeyadmamoun.network.model.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw payload of `GET /apis/site/v2/sports/soccer/{leagueSlug}/news?limit=50`.
 *
 * Only the fields required to render the news feed are mapped; the rest of the response is
 * skipped by `Json { ignoreUnknownKeys = true }`.
 *
 * Note: `categories` is deliberately **not** mapped. Because the feed is fetched once per
 * league, the league slug is already known from the request itself, which avoids parsing
 * several category objects per article.
 */
@Serializable
data class EspnNewsResponseDto(
    @SerialName("header")
    val header: String? = null,
    @SerialName("articles")
    val articles: List<EspnNewsArticleDto> = emptyList(),
)

@Serializable
data class EspnNewsArticleDto(
    @SerialName("id")
    val id: Long,
    @SerialName("type")
    val type: String? = null,
    @SerialName("headline")
    val headline: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("published")
    val published: String? = null,
    @SerialName("images")
    val images: List<EspnNewsImageDto> = emptyList(),
    @SerialName("links")
    val links: EspnNewsLinksDto? = null,
)

/**
 * `type` is either `"header"` (the article photo) or `"Media"` (a video thumbnail).
 */
@Serializable
data class EspnNewsImageDto(
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null,
)

@Serializable
data class EspnNewsLinksDto(
    @SerialName("web")
    val web: EspnNewsHrefDto? = null,
    @SerialName("mobile")
    val mobile: EspnNewsHrefDto? = null,
)

@Serializable
data class EspnNewsHrefDto(
    @SerialName("href")
    val href: String? = null,
)
