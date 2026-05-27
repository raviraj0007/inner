package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProfitChart(
    totalProfit: Double,
    winRate: Double,
    lossRate: Double,
    neutralRate: Double,
    modifier: Modifier = Modifier,
    centerLabel: String = "Net Profit"
) {
    val animateStroke = remember { Animatable(0f) }

    LaunchedEffect(key1 = totalProfit) {
        animateStroke.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    // Color theme matching the Cosmic Amber/Sage/Lavender palette
    val colorPrimary = MaterialTheme.colorScheme.primary // Amber
    val colorSecondary = MaterialTheme.colorScheme.secondary // Lavender
    val colorTertiary = MaterialTheme.colorScheme.tertiary // Sage

    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 14.dp.toPx()
            
            // Draw background track
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.minDimension / 2 - strokeWidth / 2,
                style = Stroke(width = strokeWidth)
            )

            // Dynamic segments based on proportion
            val startAngle = -90f
            val winAngle = (winRate * 360f / 100f).toFloat() * animateStroke.value
            val lossAngle = (lossRate * 360f / 100f).toFloat() * animateStroke.value
            val neutralAngle = (neutralRate * 360f / 100f).toFloat() * animateStroke.value

            // Draw win segment (Amber-Gold)
            drawArc(
                color = colorPrimary,
                startAngle = startAngle,
                sweepAngle = winAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw loss segment (Lavender)
            if (lossAngle > 0) {
                drawArc(
                    color = colorSecondary,
                    startAngle = startAngle + winAngle + 2f,
                    sweepAngle = lossAngle - 2f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Draw neutral/other segment (Sage)
            if (neutralAngle > 0) {
                drawArc(
                    color = colorTertiary,
                    startAngle = startAngle + winAngle + lossAngle + 4f,
                    sweepAngle = neutralAngle - 4f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerLabel,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (totalProfit >= 0) "$${String.format("%,.0f", totalProfit)}" else "-$${String.format("%,.0f", Math.abs(totalProfit))}",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${winRate.toInt()}% Win Rate",
                fontSize = 11.sp,
                color = colorSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
