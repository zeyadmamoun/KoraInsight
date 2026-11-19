package com.zeyadmamoun.matchdetails.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.matchdetails.R
import com.zeyadmamoun.matchdetails.components.AiMatchAnalysisCard
import com.zeyadmamoun.matchdetails.components.AiModelChatBox
import com.zeyadmamoun.matchdetails.components.CollapsedMatchDetailsAppBar
import com.zeyadmamoun.matchdetails.components.ExpandedMatchDetailsAppBar
import com.zeyadmamoun.matchdetails.components.MatchDetailsSegments
import com.zeyadmamoun.matchdetails.components.MatchTimelineCard
import com.zeyadmamoun.matchdetails.components.ModelDownloadBottomSheet
import com.zeyadmamoun.matchdetails.components.SingleChoiceSegmentedButton

/**
 * Displays match details with a collapsible score header, segmented tab views
 * (Summary, Lineup, Table, Analytics), and on-device AI tactical analysis.
 *
 * @param matchId Identifier for the match.
 * @param leagueSlug League slug identifier (e.g. "eng.1").
 * @param onBackClick Callback invoked when navigating back.
 * @param viewModel ViewModel providing match details and network status.
 * @param analyticsViewModel ViewModel managing on-device AI model download and analytics state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    matchId: String,
    leagueSlug: String,
    onBackClick: () -> Unit = {},
    viewModel: MatchDetailsViewModel = viewModel(),
    analyticsViewModel: MatchAnalyticsViewModel = viewModel()
) {
    LaunchedEffect(matchId, leagueSlug) {
        if (matchId.isNotBlank()) {
            viewModel.loadMatchDetails(matchId, leagueSlug)
        }
    }

    val context = LocalContext.current

    // Register BroadcastReceiver to listen for DownloadManager completion
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context?, intent: Intent?) {
                if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    analyticsViewModel.onDownloadCompleted()
                }
            }
        }

        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(
            context,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val analyticsUiState by analyticsViewModel.uiState.collectAsStateWithLifecycle()
    val matchDetails = uiState.matchDetails ?: MatchDetails(
        id = matchId,
        leagueSlug = leagueSlug
    )

    // Track whether the expanded header is collapsed via scroll
    var isCollapsed by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val collapseThresholdPx = with(density) { 50.dp.toPx() }
    var accumulatedScroll by remember { mutableFloatStateOf(0f) }

    var showBottomSheet by remember { mutableStateOf(false) }

    // Auto-dismiss the sheet once download finishes successfully
    LaunchedEffect(analyticsUiState.isModelDownloaded) {
        if (analyticsUiState.isModelDownloaded) {
            showBottomSheet = false
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                accumulatedScroll += available.y
                if (accumulatedScroll < -collapseThresholdPx) {
                    isCollapsed = true
                } else if (accumulatedScroll > collapseThresholdPx) {
                    isCollapsed = false
                    accumulatedScroll = 0f
                }
                return Offset.Zero
            }
        }
    }

    val cornerRadius by animateDpAsState(
        targetValue = if (isCollapsed) 0.dp else 16.dp,
        label = "AppBarCornerRadius"
    )
    val appBarShape = RoundedCornerShape(
        bottomStart = cornerRadius,
        bottomEnd = cornerRadius
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopAppBar: always visible with back button
        TopAppBar(
            actions = {
                IconButton(onClick = {}) { Icon(imageVector = Icons.Filled.Notifications, contentDescription = "follow") }
                IconButton(onClick = {}) { Icon(imageVector = Icons.Filled.Share, contentDescription = "follow") }
            },
            windowInsets = WindowInsets(top = 0.dp),
            colors = TopAppBarDefaults.topAppBarColors().copy(
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            title = {
                if (isCollapsed && uiState.networkStatus is NetworkStatus.Success) {
                    CollapsedMatchDetailsAppBar(matchDetails = matchDetails)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        when (val state = uiState.networkStatus) {
            is NetworkStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is NetworkStatus.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        ElevatedAssistChip(
                            onClick = { viewModel.loadMatchDetails(matchId, leagueSlug, isForceRefresh = true) },
                            label = { Text("Retry") }
                        )
                    }
                }
            }
            is NetworkStatus.Success -> {
                val currentMatch = uiState.matchDetails ?: MatchDetails()

                // Expanded header: shows teams + scores + goals, collapses away on scroll
                AnimatedVisibility(
                    visible = !isCollapsed,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(appBarShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        ExpandedMatchDetailsAppBar(
                            matchDetails = currentMatch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                        )
                    }
                }

                var selectedSegmentIndex by remember { mutableIntStateOf(0) }

                SingleChoiceSegmentedButton(
                    selectedSegmentIndex = selectedSegmentIndex,
                    onSegmentSelected = { selectedSegmentIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (
                        MatchDetailsSegments.entries[selectedSegmentIndex]
                    ) {
                        MatchDetailsSegments.Summary -> {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 16.dp)
                            ) {
                                item {
                                    MatchTimelineCard(currentMatch)
                                }
                            }
                        }

                        MatchDetailsSegments.Lineup -> {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 16.dp)
                            ) {
                                item { Spacer(Modifier.height(600.dp)) }
                            }
                        }

                        MatchDetailsSegments.Table -> {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 16.dp)
                            ) {
                                item { Spacer(Modifier.height(600.dp)) }
                            }
                        }

                        MatchDetailsSegments.Analytics -> {
                            if (analyticsUiState.isModelDownloaded) {
                                AiModelChatBox(
                                    modifier = Modifier.fillMaxSize(),
                                    onSendMessage = { message ->
                                        analyticsViewModel.sendMessage(message)
                                    },
                                    onMessageBoxChange = { message ->
                                        analyticsViewModel.updateMessageBoxText(message)
                                    },
                                    messageBoxText = analyticsUiState.messageBoxText,
                                    conversation = analyticsUiState.conversations
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    AiMatchAnalysisCard(
                                        onAnalyzeClick = {
                                            showBottomSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showBottomSheet){
            ModelDownloadBottomSheet(
                isDownloading = analyticsUiState.isDownloadingModel,
                downloadPercentage = analyticsUiState.downloadProgress,
                animationRes = R.raw.korainsight_btm_sheet,
                onConfirmClick = {
                    if (analyticsUiState.isDownloadingModel){
                        analyticsViewModel.cancelModelDownloading()
                    } else {
                        analyticsViewModel.downloadAnalysisModel()
                    }
                },
                onDismissClick = { selection -> showBottomSheet = selection },
            )
        }
    }
}
