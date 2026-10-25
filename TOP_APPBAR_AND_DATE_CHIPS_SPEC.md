# KoraInsight: Top App Bar & Date Chips Specification

Extracted directly from Stitch Screen `a69d5fec1dfc4450abf8e496633dfeb7` (Project `17212841064915264817`).  
This document contains **only** the specifications for the **Top App Bar** and **Date Navigation Chips**, including geometry, typography, light & dark theme color tokens, and ready-to-copy Jetpack Compose implementations.

---

## 1. Typography & Font Specifications

- **Font Family:** `Plus Jakarta Sans` (Google Fonts)
- **Fallback:** System Sans-Serif (`FontFamily.SansSerif`)

### Style Hierarchy for These Components

| Token / Usage | Font Size | Line Height | Font Weight | Letter Spacing |
| :--- | :--- | :--- | :--- | :--- |
| **App Title** (`headline-sm`) | `20.sp` | `28.sp` | SemiBold (`600`) | `-0.2.sp` (`-0.01em`) |
| **Date Chip Label** (`label-md`) | `13.sp` / `14.sp` | `18.sp` | SemiBold (`600`) | `+0.28.sp` (`+0.02em`) |

---

## 2. Top App Bar Specification

### Visual Anatomy
```
[ (KoraInsight •) ] --------------------------- [ (Search)  (Bell •)  (Avatar) ]
```

### Dimensions & Structure
- **Total Bar Height:** `64.dp`
- **Horizontal Content Insets:** `16.dp` left and right
- **Elevation / Background:** Subtle boundary border (`1.dp`) or soft ambient shadow (`2.dp` tonal elevation)

### Sub-Elements Breakdown

#### A. Left: Brand Title & Live Dot
- **Brand Name:** `"KoraInsight"`
  - Font: `Plus Jakarta Sans`, `20.sp`, `FontWeight.SemiBold`
- **Accent Dot:**
  - Diameter: `8.dp` circle
  - Spacing from text: `6.dp` horizontal gap, vertically aligned to the bottom baseline

#### B. Right: Action Icons & Profile
- **Gap between items:** `8.dp`
- **Search Button:**
  - Touch Target: `44.dp × 44.dp` circular clickable area
  - Icon: `Icons.Outlined.Search` (Size: `22.dp`)
- **Notification Button with Unread Dot:**
  - Touch Target: `44.dp × 44.dp` circular clickable area
  - Icon: `Icons.Outlined.Notifications` (Size: `22.dp`)
  - Unread Badge: `8.dp × 8.dp` circle anchored at top-right (offset `x = -10.dp`, `y = 10.dp`)
- **Profile Avatar:**
  - Container: `32.dp × 32.dp` circle
  - Icon: `Icons.Filled.Person` (Size: `18.dp`) centered inside container

---

## 3. Date Navigation Chips Specification

### Visual Anatomy
```
( Yesterday )   [ Today • ]   ( Tomorrow )   ( Sat 26 )   ( Sun 27 )
```

### Layout & Spacing
- **Container:** Horizontal scrollable `LazyRow`
- **Gap between chips:** `8.dp`
- **Content Padding:** `horizontal = 16.dp`, `vertical = 8.dp`
- **Chip Shape:** Full pill capsule (`CircleShape` / `9999px`)
- **Chip Padding:** `horizontal = 16.dp`, `vertical = 8.dp`
- **Chip Height:** `36.dp` to `38.dp`

### Chip States

#### A. Active Chip (`"Today"`)
- **Pill Background:** Solid primary blue (Light) / soft sky blue (Dark)
- **Label Text:** `"Today"`, `13.sp`, `FontWeight.SemiBold`
- **Live Match Indicator Dot:**
  - Diameter: `6.dp` circle
  - Spacing: `6.dp` gap placed after the `"Today"` text
  - Color: Coral accent (indicates live games taking place today)
- **Elevation:** `2.dp` soft drop shadow

#### B. Inactive Chips (`"Yesterday"`, `"Tomorrow"`, `"Sat 26"`, etc.)
- **Pill Background:** Pure card surface white (Light) / dark slate surface (Dark)
- **Label Text:** `13.sp`, `FontWeight.SemiBold`, muted slate color
- **Border / Outline:** `1.dp` solid border matching subtle outline token
- **Elevation:** `1.dp` to `2.dp` soft drop shadow

---

## 4. Complete Color Tokens (Light & Dark Mode)

| UI Element | Light Mode Hex | Dark Mode Hex | Matching Compose Token |
| :--- | :--- | :--- | :--- |
| **Top App Bar Surface** | `#F6F8FE` (or `#FFFFFF`) | `#0F141C` (or `#181F2A`) | `MaterialTheme.colorScheme.background` / `surface` |
| **App Title Text** | `#1E242B` | `#E6EDF5` | `MaterialTheme.colorScheme.onBackground` |
| **Title Accent Dot** | `#3463CC` | `#90B4FF` | `MaterialTheme.colorScheme.primary` |
| **Action Icons Tint** | `#8D98A5` | `#94A3B8` | `MaterialTheme.colorScheme.onSurfaceVariant` |
| **Action Icon Hover/Pressed** | `#E8EEFC` | `#222B38` | `MaterialTheme.colorScheme.surfaceVariant` |
| **Notification Badge Dot** | `#F05369` | `#FFB2BC` | `MaterialTheme.colorScheme.secondary` |
| **Avatar Circle Background** | `#3463CC` | `#90B4FF` | `MaterialTheme.colorScheme.primary` |
| **Avatar Icon Tint** | `#FFFFFF` | `#0F1E40` | `MaterialTheme.colorScheme.onPrimary` |
| **Active Date Pill Background** | `#3463CC` | `#90B4FF` | `MaterialTheme.colorScheme.primary` |
| **Active Date Pill Text** | `#FFFFFF` | `#0F1E40` | `MaterialTheme.colorScheme.onPrimary` |
| **Active Date Live Dot** | `#F05369` | `#FFB2BC` | `MaterialTheme.colorScheme.secondary` |
| **Inactive Date Pill Background**| `#FFFFFF` | `#181F2A` | `MaterialTheme.colorScheme.surface` |
| **Inactive Date Pill Text** | `#8D98A5` | `#94A3B8` | `MaterialTheme.colorScheme.onSurfaceVariant` |
| **Inactive Date Pill Border** | `#E2E8F0` | `#2E3846` | `MaterialTheme.colorScheme.outline` |

---

## 5. Developer Jetpack Compose Implementation

You can copy this directly into your project:

```kotlin
package com.zeyadmamoun.korainsight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeyadmamoun.korainsight.ui.theme.KoraInsightTheme

// =========================================================================
// 1. TOP APP BAR COMPONENT
// =========================================================================

@Composable
fun KoraTopAppBar(
    title: String = "KoraInsight",
    hasUnreadNotifications: Boolean = true,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Brand Logo & Status Dot
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                // Brand accent dot
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Right: Actions (Search, Notifications, Profile)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Search Action
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Notification Action with Badge Dot
                Box(contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // User Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 2. DATE NAVIGATION CHIPS COMPONENT
// =========================================================================

data class DateItem(
    val label: String,
    val hasLiveIndicator: Boolean = false
)

val defaultDateItems = listOf(
    DateItem("Yesterday"),
    DateItem("Today", hasLiveIndicator = true),
    DateItem("Tomorrow"),
    DateItem("Sat 26"),
    DateItem("Sun 27")
)

@Composable
fun DateSelectorRow(
    dates: List<DateItem> = defaultDateItems,
    selectedIndex: Int = 1,
    onDateSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(dates) { index, item ->
            val isSelected = selectedIndex == index
            DateChip(
                dateItem = item,
                isSelected = isSelected,
                onClick = { onDateSelected(index) }
            )
        }
    }
}

@Composable
fun DateChip(
    dateItem: DateItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderModifier = if (isSelected) {
        Modifier
    } else {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = CircleShape
        )
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 2.dp else 1.dp,
                shape = CircleShape
            )
            .then(borderModifier)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = dateItem.label,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.28.sp
            )

            // Optional live dot (shown when active)
            if (dateItem.hasLiveIndicator && isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

// =========================================================================
// 3. PREVIEWS (LIGHT & DARK THEMES)
// =========================================================================

@Preview(name = "Light Mode", showBackground = true, backgroundColor = 0xFFF6F8FE)
@Composable
fun TopBarAndDateChipsLightPreview() {
    KoraInsightTheme(darkTheme = false) {
        androidx.compose.foundation.layout.Column {
            KoraTopAppBar()
            DateSelectorRow(selectedIndex = 1)
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, backgroundColor = 0xFF0F141C)
@Composable
fun TopBarAndDateChipsDarkPreview() {
    KoraInsightTheme(darkTheme = true) {
        androidx.compose.foundation.layout.Column {
            KoraTopAppBar()
            DateSelectorRow(selectedIndex = 1)
        }
    }
}
```
