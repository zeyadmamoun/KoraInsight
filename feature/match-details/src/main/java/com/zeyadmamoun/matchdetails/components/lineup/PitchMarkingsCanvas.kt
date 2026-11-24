package com.zeyadmamoun.matchdetails.components.lineup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard football pitch canvas with regulation markings and grass textures.
 *
 * All geometry (penalty areas, goal boxes, center circle, penalty arcs, and corners)
 * is mathematically proportioned according to real FIFA pitch dimensions (105m x 68m).
 *
 * @param modifier Modifier applied to the Canvas container.
 * @param pitchBackgroundColors Vertical gradient for the pitch turf.
 * @param lineColor Color of all field lines and markings.
 * @param lineWidth Thickness of pitch lines (defaults to 1.5.dp).
 * @param showMowingStripes If true, renders subtle alternating turf mowing bands.
 * @param stripeCount Number of horizontal mowing grass stripes.
 */
@Composable
fun PitchMarkingsCanvas(
    modifier: Modifier = Modifier,
    pitchBackgroundColors: List<Color> = listOf(
        Color(0xFF144227), // Deep tactical grass green (top)
        Color(0xFF1B5433), // Slightly lighter green (center)
        Color(0xFF144227)  // Deep tactical grass green (bottom)
    ),
    lineColor: Color = Color.White.copy(alpha = 0.35f),
    lineWidth: Dp = 1.5.dp,
    showMowingStripes: Boolean = true,
    stripeCount: Int = 10
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(pitchBackgroundColors))
    ) {
        val w = size.width
        val h = size.height

        val strokeWidthPx = lineWidth.toPx()
        val stroke = Stroke(width = strokeWidthPx)

        // -------------------------------------------------------------
        // 1. Stadium Grass Mowing Stripes (Alternating bands)
        // -------------------------------------------------------------
        if (showMowingStripes && stripeCount > 0) {
            val stripeHeight = h / stripeCount
            for (i in 0 until stripeCount step 2) {
                drawRect(
                    color = Color.White.copy(alpha = 0.035f),
                    topLeft = Offset(0f, i * stripeHeight),
                    size = Size(w, stripeHeight)
                )
            }
        }

        // Pitch inset boundary (padding from canvas edges)
        val inset = 8.dp.toPx()
        val pitchLeft = inset
        val pitchTop = inset
        val pitchRight = w - inset
        val pitchBottom = h - inset
        val pitchW = pitchRight - pitchLeft
        val pitchH = pitchBottom - pitchTop

        val centerX = pitchLeft + (pitchW / 2f)
        val centerY = pitchTop + (pitchH / 2f)

        // -------------------------------------------------------------
        // 2. Outer Boundary & Halfway Line
        // -------------------------------------------------------------
        // Boundary rectangle
        drawRect(
            color = lineColor,
            topLeft = Offset(pitchLeft, pitchTop),
            size = Size(pitchW, pitchH),
            style = stroke
        )

        // Halfway line
        drawLine(
            color = lineColor,
            start = Offset(pitchLeft, centerY),
            end = Offset(pitchRight, centerY),
            strokeWidth = strokeWidthPx
        )

        // -------------------------------------------------------------
        // 3. Center Circle & Center Spot
        // -------------------------------------------------------------
        // Center circle radius: 9.15m on a 68m wide pitch (~13.5% of pitch width)
        val centerCircleRadius = pitchW * 0.14f

        drawCircle(
            color = lineColor,
            radius = centerCircleRadius,
            center = Offset(centerX, centerY),
            style = stroke
        )

        // Center spot
        drawCircle(
            color = lineColor,
            radius = 2.5.dp.toPx(),
            center = Offset(centerX, centerY)
        )

        // -------------------------------------------------------------
        // 4. Penalty Areas (18-Yard Box) - Top & Bottom
        // -------------------------------------------------------------
        // 40.32m width (~58% of pitch width), 16.5m depth (~16% of pitch height)
        val penaltyBoxW = pitchW * 0.58f
        val penaltyBoxH = pitchH * 0.16f
        val penaltyBoxLeft = centerX - (penaltyBoxW / 2f)

        // Top Penalty Box (Away goal)
        drawRect(
            color = lineColor,
            topLeft = Offset(penaltyBoxLeft, pitchTop),
            size = Size(penaltyBoxW, penaltyBoxH),
            style = stroke
        )

        // Bottom Penalty Box (Home goal)
        drawRect(
            color = lineColor,
            topLeft = Offset(penaltyBoxLeft, pitchBottom - penaltyBoxH),
            size = Size(penaltyBoxW, penaltyBoxH),
            style = stroke
        )

        // -------------------------------------------------------------
        // 5. Goal Areas (6-Yard Box) - Top & Bottom
        // -------------------------------------------------------------
        // 18.32m width (~28% of pitch width), 5.5m depth (~5.5% of pitch height)
        val goalAreaW = pitchW * 0.28f
        val goalAreaH = pitchH * 0.055f
        val goalAreaLeft = centerX - (goalAreaW / 2f)

        // Top 6-yard Box
        drawRect(
            color = lineColor,
            topLeft = Offset(goalAreaLeft, pitchTop),
            size = Size(goalAreaW, goalAreaH),
            style = stroke
        )

        // Bottom 6-yard Box
        drawRect(
            color = lineColor,
            topLeft = Offset(goalAreaLeft, pitchBottom - goalAreaH),
            size = Size(goalAreaW, goalAreaH),
            style = stroke
        )

        // -------------------------------------------------------------
        // 6. Penalty Spots & Penalty Arcs (The "D")
        // -------------------------------------------------------------
        // Penalty spot is 11m from goal line (~10.5% of pitch height)
        val penaltySpotOffset = pitchH * 0.105f
        val topPenaltySpotY = pitchTop + penaltySpotOffset
        val bottomPenaltySpotY = pitchBottom - penaltySpotOffset

        // Top & Bottom Penalty Spots
        drawCircle(
            color = lineColor,
            radius = 2.5.dp.toPx(),
            center = Offset(centerX, topPenaltySpotY)
        )
        drawCircle(
            color = lineColor,
            radius = 2.5.dp.toPx(),
            center = Offset(centerX, bottomPenaltySpotY)
        )

        // Penalty Arc has a radius of 9.15m (~centerCircleRadius)
        val arcRadius = centerCircleRadius
        val arcDiameter = arcRadius * 2f

        // Top Penalty Arc (faces downward outside the 18-yard box)
        drawArc(
            color = lineColor,
            startAngle = 37f,
            sweepAngle = 106f,
            useCenter = false,
            topLeft = Offset(centerX - arcRadius, topPenaltySpotY - arcRadius),
            size = Size(arcDiameter, arcDiameter),
            style = stroke
        )

        // Bottom Penalty Arc (faces upward outside the 18-yard box)
        drawArc(
            color = lineColor,
            startAngle = 217f,
            sweepAngle = 106f,
            useCenter = false,
            topLeft = Offset(centerX - arcRadius, bottomPenaltySpotY - arcRadius),
            size = Size(arcDiameter, arcDiameter),
            style = stroke
        )

        // -------------------------------------------------------------
        // 7. Corner Arcs (All 4 Corners)
        // -------------------------------------------------------------
        val cornerArcRadius = 8.dp.toPx()
        val cornerArcDiameter = cornerArcRadius * 2f

        // Top-Left Corner
        drawArc(
            color = lineColor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(pitchLeft - cornerArcRadius, pitchTop - cornerArcRadius),
            size = Size(cornerArcDiameter, cornerArcDiameter),
            style = stroke
        )

        // Top-Right Corner
        drawArc(
            color = lineColor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(pitchRight - cornerArcRadius, pitchTop - cornerArcRadius),
            size = Size(cornerArcDiameter, cornerArcDiameter),
            style = stroke
        )

        // Bottom-Right Corner
        drawArc(
            color = lineColor,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(pitchRight - cornerArcRadius, pitchBottom - cornerArcRadius),
            size = Size(cornerArcDiameter, cornerArcDiameter),
            style = stroke
        )

        // Bottom-Left Corner
        drawArc(
            color = lineColor,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(pitchLeft - cornerArcRadius, pitchBottom - cornerArcRadius),
            size = Size(cornerArcDiameter, cornerArcDiameter),
            style = stroke
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun PitchMarkingsCanvasPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        PitchMarkingsCanvas()
    }
}
