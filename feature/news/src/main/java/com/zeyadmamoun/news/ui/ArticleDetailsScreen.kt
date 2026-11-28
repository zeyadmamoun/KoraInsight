package com.zeyadmamoun.news.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zeyadmamoun.core.common.datetime.toRelativeTimeAgo
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.data.model.news.ArticleDetails
import com.zeyadmamoun.news.ui.components.ErrorNewsPlaceholder
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

private val DetailMediaBadgeColor = Color(0xFFF05369)
private val ShortMonthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

/**
 * Stateful Article Details screen wired to [ArticleDetailsViewModel].
 */
@Composable
fun ArticleDetailsScreen(
    viewModel: ArticleDetailsViewModel,
    onBackClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ArticleDetailsContent(
        state = state,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onRetry = { viewModel.loadArticleDetails() },
        onShareClick = { url, headline -> shareArticleUrl(context, url, headline) },
        onOpenUrl = { url -> openExternalUrl(context, url) }
    )
}

/**
 * Stateless Article Details UI matching the Light and Dark Figma layouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleDetailsContent(
    state: ArticleDetailsUiState,
    onBackClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onRetry: () -> Unit = {},
    onShareClick: (url: String, headline: String) -> Unit = { _, _ -> },
    onOpenUrl: (url: String) -> Unit = {},
) {
    val article = state.article

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            windowInsets = WindowInsets(top = 0.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            title = {
                Text(
                    text = "Article",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            actions = {
                val shareUrl = article?.espnUrl
                if (!shareUrl.isNullOrBlank()) {
                    IconButton(onClick = { onShareClick(shareUrl, article.headline) }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share article"
                        )
                    }
                    IconButton(onClick = { onOpenUrl(shareUrl) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open in browser"
                        )
                    }
                }
            }
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val status = state.networkStatus) {
                is NetworkStatus.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is NetworkStatus.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorNewsPlaceholder(
                            message = status.message,
                            onRetry = onRetry
                        )
                    }
                }

                is NetworkStatus.Success -> {
                    if (article != null) {
                        ArticleReadingBody(
                            article = article,
                            onOpenUrl = onOpenUrl
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleReadingBody(
    article: ArticleDetails,
    onOpenUrl: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "intro") {
            ArticleIntroSection(article = article)
        }

        if (!article.byline.isNullOrBlank() || article.lastModifiedAt != null || article.publishedAt != null) {
            item(key = "author") {
                AuthorCard(
                    byline = article.byline ?: "ESPN News Services",
                    updatedInstant = article.lastModifiedAt ?: article.publishedAt
                )
            }
        }

        if (article.topics.isNotEmpty()) {
            item(key = "topics") {
                TopicTagsRow(topics = article.topics)
            }
        }

        if (!article.heroImageUrl.isNullOrBlank() || !article.videoUrl.isNullOrBlank()) {
            item(key = "media") {
                ArticleMediaSection(
                    imageUrl = article.heroImageUrl,
                    headline = article.headline,
                    isVideo = article.isVideo && !article.videoUrl.isNullOrBlank(),
                    durationSeconds = article.videoDurationSeconds,
                    caption = article.heroCaption,
                    credit = article.heroCredit,
                    onPlayClick = {
                        val targetUrl = article.videoUrl ?: article.espnUrl
                        if (!targetUrl.isNullOrBlank()) {
                            onOpenUrl(targetUrl)
                        }
                    }
                )
            }
        }

        if (article.storyParagraphs.isNotEmpty()) {
            item(key = "divider") {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),
                    thickness = 1.dp
                )
            }

            itemsIndexed(
                items = article.storyParagraphs,
                key = { index, _ -> "paragraph_$index" }
            ) { _, paragraph ->
                Text(
                    text = paragraph,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        article.espnUrl?.takeIf { it.isNotBlank() }?.let { espnUrl ->
            item(key = "continue_reading") {
                Spacer(modifier = Modifier.height(4.dp))
                ContinueReadingButton(onClick = { onOpenUrl(espnUrl) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ArticleIntroSection(article: ArticleDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (article.isVideo) {
                Box(
                    modifier = Modifier
                        .background(DetailMediaBadgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MEDIA",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            val metaParts = listOfNotNull(
                article.leagueName.takeIf { it.isNotBlank() }?.uppercase(),
                article.publishedAt?.toRelativeTimeAgo()
            ).joinToString(" · ")

            if (metaParts.isNotEmpty()) {
                Text(
                    text = metaParts,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = article.headline,
            fontSize = 22.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        article.description?.let { description ->
            Text(
                text = description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AuthorCard(
    byline: String,
    updatedInstant: Instant?,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = byline,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                updatedInstant?.let { instant ->
                    Text(
                        text = "Updated ${instant.toUpdatedDateLabel()}",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicTagsRow(topics: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = topics, key = { it }) { topic ->
            Surface(
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = topic,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleMediaSection(
    imageUrl: String?,
    headline: String,
    isVideo: Boolean,
    durationSeconds: Int?,
    caption: String?,
    credit: String?,
    onPlayClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val mediaShape = RoundedCornerShape(16.dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(mediaShape)
                .then(if (isVideo) Modifier.clickable(onClick = onPlayClick) else Modifier),
            shape = mediaShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(44.dp)
                            .background(Color(0x941E242B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    durationSeconds?.takeIf { it > 0 }?.let { seconds ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .background(Color(0xBF1E242B), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formatVideoDuration(seconds),
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (!caption.isNullOrBlank() || !credit.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                caption?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                credit?.let {
                    Text(
                        text = "Photo: $it",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueReadingButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "Continue reading on ESPN",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Instant.toUpdatedDateLabel(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val date = toLocalDateTime(timeZone).date
    val month = ShortMonthNames.getOrElse(date.month.ordinal) { "" }
    return "$month ${date.day}, ${date.year}"
}

private fun formatVideoDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun openExternalUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    runCatching { context.startActivity(intent) }
}

private fun shareArticleUrl(context: Context, url: String, headline: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, headline)
        putExtra(Intent.EXTRA_TEXT, "$headline\n$url")
    }
    val chooser = Intent.createChooser(sendIntent, "Share article")
    runCatching { context.startActivity(chooser) }
}

private val PreviewArticleDetails = ArticleDetails(
    id = "50615700",
    headline = "Alonso urges Chelsea to 'stick together' amid run of three losses",
    description = "Unity is the message for Chelsea as attention turns to a response after three straight defeats.",
    leagueName = "Premier League",
    byline = "ESPN News Services",
    publishedAt = Clock.System.now() - 2.hours,
    lastModifiedAt = Clock.System.now(),
    isVideo = true,
    heroCaption = "Alonso calls for Chelsea to 'stick together'.",
    heroCredit = "Getty Images",
    videoUrl = "https://www.espn.com",
    videoDurationSeconds = 75,
    topics = listOf("Chelsea", "Premier League"),
    storyParagraphs = listOf(
        "Three successive losses have put the focus on Chelsea's response. Alonso's message is one of togetherness as the side looks to turn its form around.",
        "The challenge now is to translate that message into a more assured performance and bring the losing run to an end."
    ),
    espnUrl = "https://www.espn.com"
)

@Preview(uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun ArticleDetailsLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF3463CC),
            onPrimary = Color.White,
            background = Color(0xFFF6F8FE),
            surface = Color.White,
            onSurface = Color(0xFF1E242B),
            surfaceVariant = Color(0xFFE2E5EB),
            onSurfaceVariant = Color(0xFF8D98A5),
            outline = Color(0xFFE2E8F0)
        )
    ) {
        ArticleDetailsContent(
            state = ArticleDetailsUiState(
                networkStatus = NetworkStatus.Success,
                article = PreviewArticleDetails
            )
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ArticleDetailsDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF3463CC),
            onPrimary = Color.White,
            background = Color(0xFF0F141C),
            surface = Color(0xFF181F2A),
            onSurface = Color(0xFFE6EDF5),
            surfaceVariant = Color(0xFF222E42),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF3A4658)
        )
    ) {
        ArticleDetailsContent(
            state = ArticleDetailsUiState(
                networkStatus = NetworkStatus.Success,
                article = PreviewArticleDetails
            )
        )
    }
}
