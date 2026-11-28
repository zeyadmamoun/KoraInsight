package com.zeyadmamoun.matchdetails.components

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zeyadmamoun.matchdetails.R
import kotlinx.coroutines.launch
import androidx.compose.material3.LinearWavyProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDownloadBottomSheet(
    @RawRes animationRes: Int,
    isDownloading: Boolean = false,
    downloadPercentage: Int = 0,
    onDismissClick: (Boolean) -> Unit = {},
    onConfirmClick: (Boolean) -> Unit = {}
) {
    val stateSheet = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = stateSheet,
        onDismissRequest = {
            scope.launch {
                stateSheet.hide()
                onDismissClick(false)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Lottie File Animation
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Description
            if (isDownloading){
                DownloadingStateSection(downloadPercentage)
            } else {
                NotDownloadedModelDesc()
            }

            // 3. Confirmation Button
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (isDownloading) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                    scope.launch {
                        onConfirmClick(false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isDownloading)
                        stringResource(R.string.cancel_download)
                    else
                        stringResource(R.string.confirm_download),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DownloadingStateSection(
    downloadPercentage: Int = 0
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(
            text = "Download Local Model",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearWavyProgressIndicator(
            progress = { downloadPercentage / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun NotDownloadedModelDesc(){
    Text(
        text = "Download Local Model",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.analysis_model_download_desc, 2232),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(28.dp))
}

