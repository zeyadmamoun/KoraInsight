package com.zeyadmamoun.matchdetails.ui

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.matchdetails.components.AiMatchAnalysisCard
import com.zeyadmamoun.matchdetails.components.CollapsedMatchDetailsAppBar
import com.zeyadmamoun.matchdetails.components.ExpandedMatchDetailsAppBar
import com.zeyadmamoun.matchdetails.components.MatchDetailsSegments
import com.zeyadmamoun.matchdetails.components.MatchTimelineCard
import com.zeyadmamoun.matchdetails.components.ModelDownloadBottomSheet
import com.zeyadmamoun.matchdetails.components.SingleChoiceSegmentedButton

/**
 * Starter screen for the Match Details feature.
 *
 * @param matchId Identifier for the match.
 * @param leagueSlug League slug identifier (e.g. "eng.1").
 * @param onBackClick Callback invoked when navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailsScreen(
    matchId: String,
    leagueSlug: String,
    onAnalyzeClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: MatchDetailsViewModel = viewModel()
) {
    LaunchedEffect(matchId, leagueSlug) {
        if (matchId.isNotBlank()) {
            viewModel.loadMatchDetails(matchId, leagueSlug)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val matchDetails = (uiState as? MatchDetailsUiState.Success)?.matchDetails ?: MatchDetails(
        id = matchId,
        leagueSlug = leagueSlug
    )

    // Track whether the expanded header is collapsed via scroll
    var isCollapsed by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val collapseThresholdPx = with(density) { 50.dp.toPx() }
    var accumulatedScroll by remember { mutableFloatStateOf(0f) }

    var showBottomSheet by remember { mutableStateOf(false) }

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
                if (isCollapsed && uiState is MatchDetailsUiState.Success) {
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

        when (val state = uiState) {
            is MatchDetailsUiState.Loading -> {
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
            is MatchDetailsUiState.Error -> {
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
            is MatchDetailsUiState.Success -> {
                val currentMatch = state.matchDetails

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

                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    item {
                        SingleChoiceSegmentedButton(
                            selectedSegmentIndex = selectedSegmentIndex,
                            onSegmentSelected = { selectedSegmentIndex = it }
                        )
                    }

                    item {
                        when (
                            MatchDetailsSegments.entries[selectedSegmentIndex]
                        ) {
                            MatchDetailsSegments.Summary -> {
                                MatchTimelineCard(currentMatch)
                            }

                            MatchDetailsSegments.Lineup -> Spacer(Modifier.height(600.dp))
                            MatchDetailsSegments.Table -> Spacer(Modifier.height(600.dp))
                            MatchDetailsSegments.Analytics -> {
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
        if (showBottomSheet){
            ModelDownloadBottomSheet(
                onConfirmClick = { selection -> showBottomSheet = selection },
                onDismissClick = { selection -> showBottomSheet = selection },
            )
        }
    }
}
