package com.zeyadmamoun.news.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val MAX_VISIBLE_PAGE_BUTTONS = 5

/**
 * Centered pill pagination bar showing up to 5 page numbers around [currentPage],
 * with previous/next chevrons when [totalPages] exceeds 5.
 */
@Composable
fun NewsPaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (totalPages <= 1) return

    val windowStart = (currentPage - 2).coerceIn(
        minimumValue = 1,
        maximumValue = (totalPages - MAX_VISIBLE_PAGE_BUTTONS + 1).coerceAtLeast(1)
    )
    val windowEnd = (windowStart + MAX_VISIBLE_PAGE_BUTTONS - 1).coerceAtMost(totalPages)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (totalPages > MAX_VISIBLE_PAGE_BUTTONS) {
            PaginationArrowButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous page",
                enabled = currentPage > 1,
                onClick = { onPageSelected(currentPage - 1) }
            )
        }

        for (page in windowStart..windowEnd) {
            PaginationPageButton(
                pageNumber = page,
                isSelected = page == currentPage,
                onClick = { onPageSelected(page) }
            )
        }

        if (totalPages > MAX_VISIBLE_PAGE_BUTTONS) {
            PaginationArrowButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next page",
                enabled = currentPage < totalPages,
                onClick = { onPageSelected(currentPage + 1) }
            )
        }
    }
}

@Composable
private fun PaginationPageButton(
    pageNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = if (isSelected) {
            null
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = pageNumber.toString(),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun PaginationArrowButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                },
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
