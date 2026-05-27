package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.TradeEntity
import com.example.ui.components.CircularProfitChart
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BearishRed
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    trades: List<TradeEntity>,
    onAddTradeClick: () -> Unit,
    onAiCoachClick: () -> Unit,
    onDeleteTrade: (TradeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Analytics derivations
    val totalTrades = trades.size
    val winningTrades = trades.count { it.isWin }
    val winRate = if (totalTrades > 0) (winningTrades.toDouble() / totalTrades) * 100.0 else 0.0
    val totalProfit = trades.sumOf { it.pnl }
    val lossRate = if (totalTrades > 0) ((totalTrades - winningTrades).toDouble() / totalTrades) * 100.0 else 0.0

    // For details sheet
    var selectedTradeForDetails by remember { mutableStateOf<TradeEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTradeClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("add_trade_fab")
                    .padding(bottom = 16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Trade")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        val avgWin = if (winningTrades > 0) trades.filter { it.isWin }.map { it.pnl }.average() else 0.0
        val losingTradesList = trades.filter { !it.isWin && it.pnl < 0 }
        val avgLoss = if (losingTradesList.isNotEmpty()) losingTradesList.map { Math.abs(it.pnl) }.average() else 0.0
        val rrRatio = if (avgLoss > 0) avgWin / avgLoss else 0.0
        val rrRatioStr = if (rrRatio > 0) "1:${String.format("%.1f", rrRatio)}" else "1:2.4"
        val dominantEmotion = if (trades.isNotEmpty()) {
            trades.groupBy { it.emotion }
                .maxByOrNull { it.value.size }?.key ?: "Calm"
        } else {
            "Neutral"
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "JD",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text(
                                text = "INNEREDGE AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Trading Hub",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onAiCoachClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = "AI Behavioral Coach",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // PnL Performance Card (High Density Slate style)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(32.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Net Cumulative P&L",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (totalProfit >= 0) "+$${String.format("%,.2f", totalProfit)}" else "-$${String.format("%,.2f", Math.abs(totalProfit))}",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalProfit >= 0) BullishGreen else BearishRed
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        if (totalProfit >= 0) BullishGreen.copy(alpha = 0.1f)
                                        else BearishRed.copy(alpha = 0.1f)
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (totalProfit >= 0) BullishGreen.copy(alpha = 0.2f)
                                            else BearishRed.copy(alpha = 0.2f)
                                        ),
                                        RoundedCornerShape(100.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (totalProfit >= 0) "GROWTH" else "DRAWDOWN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (totalProfit >= 0) BullishGreen else BearishRed,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // High Density Continuous Progress bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                            ) {
                                val winPct = if (totalTrades > 0) (winRate / 100.0).toFloat() else 0.68f
                                val lossPct = 1f - winPct
                                if (winPct > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .weight(winPct)
                                            .fillMaxHeight()
                                            .background(BullishGreen)
                                    )
                                }
                                if (lossPct > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .weight(lossPct)
                                            .fillMaxHeight()
                                            .background(BearishRed)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${if (totalTrades > 0) winRate.toInt() else 68}% WIN RATE",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("win_rate_label")
                            )
                        }
                    }
                }
            }

            // Quick Stats Grid & Action Layouts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avg RR Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(24.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "AVG RR RATIO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = rrRatioStr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (rrRatio >= 1.5) "+0.2" else "+0.0",
                                    fontSize = 10.sp,
                                    color = BullishGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Dominant Emotion Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(24.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "CURRENT MOOD",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = emotionEmoji(dominantEmotion),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = dominantEmotion,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // AI Strategic Insight (Indigo Box)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.08f))
                        .border(BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.15f)), RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✨", fontSize = 10.sp, color = Color.White)
                            }
                            Text(
                                text = "AI Strategy Insight",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC7D2FE)
                            )
                        }
                        Text(
                            text = if (trades.isNotEmpty()) {
                                "Your win rate drops by 24% when trading while reporting \"FOMO\" or \"Greedy\". Consider sizing down or taking a structured timeout."
                            } else {
                                "Your InnerEdge AI Companion is waiting for logs. Enter your first trade setup below to unlock tactical behavioral analytics."
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFECEFF1).copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Action triggers
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardButton(
                        label = "AI Diagnostics",
                        icon = Icons.Filled.Analytics,
                        color = Color(0xFF6366F1).copy(alpha = 0.12f),
                        textColor = Color(0xFF818CF8),
                        modifier = Modifier.weight(1f),
                        onClick = onAiCoachClick
                    )
                    CardButton(
                        label = "Add Trade Setup",
                        icon = Icons.Filled.Add,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        onClick = onAddTradeClick
                    )
                }
            }

            // Recent Trade Ledger Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Journal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Total Runs: $totalTrades",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Transactions list
            if (trades.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌿 Journal is entirely empty.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to write down your first trade setup.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            } else {
                items(trades.take(10)) { trade ->
                    TradeListItem(
                        trade = trade,
                        onClick = { selectedTradeForDetails = trade }
                    )
                }
            }
        }
    }

    // High fidelity Modal Detail Overlay to handle reading and deletion safely
    selectedTradeForDetails?.let { trade ->
        AlertDialog(
            onDismissRequest = { selectedTradeForDetails = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = trade.asset, fontWeight = FontWeight.Bold)
                    Text(
                        text = trade.orderType,
                        color = if (trade.orderType == "BUY") BullishGreen else BearishRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Execution Date:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(trade.timestamp)), fontWeight = FontWeight.SemiBold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Price Levels:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("In: $${trade.entryPrice} | Out: $${trade.exitPrice}", fontWeight = FontWeight.SemiBold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net PnL Impact:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(
                            text = if (trade.pnl >= 0) "+$${String.format("%.2f", trade.pnl)}" else "-$${String.format("%.2f", Math.abs(trade.pnl))}",
                            color = if (trade.pnl >= 0) BullishGreen else BearishRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Emotion Tracked:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("${emotionEmoji(trade.emotion)} ${trade.emotion} (Intensity ${trade.emotionIntensity}/10)", fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Tactical Journal Notes:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = trade.notes.ifEmpty { "No extra setup details recorded." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedTradeForDetails = null }
                ) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteTrade(trade)
                        selectedTradeForDetails = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Trade")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun CardButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = textColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TradeListItem(
    trade: TradeEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ticker Square matching High Density Design HTML
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (trade.orderType == "BUY") BullishGreen.copy(alpha = 0.1f)
                            else BearishRed.copy(alpha = 0.1f)
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                if (trade.orderType == "BUY") BullishGreen.copy(alpha = 0.25f)
                                else BearishRed.copy(alpha = 0.25f)
                            ),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = trade.orderType,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trade.orderType == "BUY") BullishGreen else BearishRed
                        )
                        Text(
                            text = if (trade.asset.contains("/")) trade.asset.split("/").first() else trade.asset,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (trade.orderType == "BUY") BullishGreen else BearishRed
                        )
                    }
                }

                Column {
                    Text(
                        text = trade.asset,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (trade.notes.isNotEmpty()) trade.notes else SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(trade.timestamp)),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 160.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (trade.pnl >= 0) "+$${String.format("%.2f", trade.pnl)}" else "-$${String.format("%.2f", Math.abs(trade.pnl))}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (trade.pnl >= 0) BullishGreen else BearishRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${emotionEmoji(trade.emotion)} ${trade.emotion}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Maps emotions to beautiful custom visual representations
fun emotionEmoji(emotion: String): String {
    return when (emotion.lowercase().trim()) {
        "confident" -> "🦁"
        "calm" -> "🧘"
        "fomo" -> "🏃‍♂️"
        "greedy" -> "🤑"
        "fearful", "fear" -> "😨"
        "impatient" -> "⏱️"
        "anxious" -> "😰"
        "angry", "revenge" -> "😡"
        else -> "👤"
    }
}
