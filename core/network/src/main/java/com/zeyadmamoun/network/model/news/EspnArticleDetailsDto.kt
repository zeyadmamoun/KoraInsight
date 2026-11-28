package com.zeyadmamoun.network.model.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw payload of `GET https://content.core.api.espn.com/v1/sports/news/{articleId}`.
 */
@Serializable
data class EspnArticleDetailsResponseDto(
    @SerialName("headlines")
    val headlines: List<EspnArticleDetailDto> = emptyList(),
)

@Serializable
data class EspnArticleDetailDto(
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
    @SerialName("lastModified")
    val lastModified: String? = null,
    @SerialName("section")
    val section: String? = null,
    @SerialName("byline")
    val byline: String? = null,
    @SerialName("story")
    val story: String? = null,
    @SerialName("images")
    val images: List<EspnDetailImageDto> = emptyList(),
    @SerialName("video")
    val video: List<EspnDetailVideoDto> = emptyList(),
    @SerialName("categories")
    val categories: List<EspnDetailCategoryDto> = emptyList(),
    @SerialName("links")
    val links: EspnNewsLinksDto? = null,
)

@Serializable
data class EspnDetailImageDto(
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("caption")
    val caption: String? = null,
    @SerialName("credit")
    val credit: String? = null,
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null,
)

@Serializable
data class EspnDetailVideoDto(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("headline")
    val headline: String? = null,
    @SerialName("duration")
    val duration: Int? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null,
    @SerialName("links")
    val links: EspnDetailVideoLinksDto? = null,
)

@Serializable
data class EspnDetailVideoLinksDto(
    @SerialName("source")
    val source: EspnDetailVideoSourceDto? = null,
    @SerialName("web")
    val web: EspnNewsHrefDto? = null,
)

@Serializable
data class EspnDetailVideoSourceDto(
    @SerialName("href")
    val href: String? = null,
    @SerialName("HD")
    val hd: EspnNewsHrefDto? = null,
    @SerialName("HLS")
    val hls: EspnNewsHrefDto? = null,
)

@Serializable
data class EspnDetailCategoryDto(
    @SerialName("type")
    val type: String? = null,
    @SerialName("description")
    val description: String? = null,
)
