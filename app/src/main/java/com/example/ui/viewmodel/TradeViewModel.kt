package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiHelper
import com.example.data.database.TradeEntity
import com.example.data.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradeViewModel(
    private val repository: TradeRepository
) : ViewModel() {

    val allTrades: StateFlow<List<TradeEntity>> = repository.getAllTrades()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _aiAnalysisState = MutableStateFlow<AiAnalysisState>(AiAnalysisState.Initial)
    val aiAnalysisState: StateFlow<AiAnalysisState> = _aiAnalysisState.asStateFlow()

    fun addTrade(
        asset: String,
        orderType: String,
        entryPrice: Double,
        exitPrice: Double,
        quantity: Double,
        emotion: String,
        emotionIntensity: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val pnl = if (orderType == "BUY") {
                (exitPrice - entryPrice) * quantity
            } else {
                (entryPrice - exitPrice) * quantity
            }
            val isWin = pnl > 0

            val trade = TradeEntity(
                asset = asset.uppercase().trim(),
                orderType = orderType,
                entryPrice = entryPrice,
                exitPrice = exitPrice,
                quantity = quantity,
                timestamp = System.currentTimeMillis(),
                pnl = pnl,
                emotion = emotion,
                emotionIntensity = emotionIntensity,
                notes = notes,
                isWin = isWin
            )
            repository.insertTrade(trade)
        }
    }

    fun deleteTrade(trade: TradeEntity) {
        viewModelScope.launch {
            repository.deleteTrade(trade)
        }
    }

    fun triggerAiPsychologyAnalysis() {
        val tradesList = allTrades.value
        if (tradesList.isEmpty()) {
            _aiAnalysisState.value = AiAnalysisState.Error("No trades logged yet. Please log a few trades first.")
            return
        }

        _aiAnalysisState.value = AiAnalysisState.Loading

        viewModelScope.launch {
            // First, compile Offline Psychological Insights (Heuristics)
            val offlineMetrics = computeOfflinePsychology(tradesList)

            // Dynamic Prompt for Gemini (if configured)
            val builder = StringBuilder()
            builder.append("Please analyze my trading journal psychological profile based on the following logs:\n\n")
            for (trade in tradesList) {
                builder.append("- ${trade.asset} (${trade.orderType}): PnL: $${String.format("%.2f", trade.pnl)}, Emotion: ${trade.emotion} (Intensity ${trade.emotionIntensity}/10), Notes: \"${trade.notes}\"\n")
            }
            builder.append("\nProvide a highly structured trading psychological blueprint in markdown format: ")
            builder.append("1. **Executive Behavioral Summary**\n")
            builder.append("2. **Core Psychological Triggers Identified** (like Fear, FOMO, or Greed)\n")
            builder.append("3. **Your Behavioral Win-Rate Formula** (correlating emotional states with profits)\n")
            builder.append("4. **3 Key Tactical Rules to Prevent Capital Leaks**\n")
            builder.append("\nEnsure your advice is brief, professional, and directly useful for future execution.")

            val aiResponse = GeminiHelper.analyzePsychology(builder.toString())

            if (aiResponse.isNotEmpty()) {
                _aiAnalysisState.value = AiAnalysisState.Success(
                    rawResponse = aiResponse,
                    insights = offlineMetrics.insights,
                    leakState = offlineMetrics.leakState,
                    leakPnl = offlineMetrics.leakPnl,
                    optimalState = offlineMetrics.optimalState,
                    optimalWinRate = offlineMetrics.optimalWinRate,
                    isGeminiPowered = true
                )
            } else {
                // Return offline diagnostics completely
                _aiAnalysisState.value = AiAnalysisState.Success(
                    rawResponse = generateOfflineMarkdownReport(offlineMetrics),
                    insights = offlineMetrics.insights,
                    leakState = offlineMetrics.leakState,
                    leakPnl = offlineMetrics.leakPnl,
                    optimalState = offlineMetrics.optimalState,
                    optimalWinRate = offlineMetrics.optimalWinRate,
                    isGeminiPowered = false
                )
            }
        }
    }

    private fun computeOfflinePsychology(trades: List<TradeEntity>): OfflineMetrics {
        val emotionGroups = trades.groupBy { it.emotion }
        var worstLeakState = "None"
        var largestLoss = 0.0
        var bestState = "None"
        var bestWinRate = -1.0
        val insightsList = mutableListOf<String>()

        for ((emotion, emotionTrades) in emotionGroups) {
            val total = emotionTrades.size
            val wins = emotionTrades.count { it.isWin }
            val winRate = (wins.toDouble() / total) * 100.0
            val totalPnl = emotionTrades.sumOf { it.pnl }

            // Track Leak: lowest PnL
            if (totalPnl < largestLoss) {
                largestLoss = totalPnl
                worstLeakState = emotion
            }

            // Track Best: highest Win Rate with at least 1 trade
            if (winRate > bestWinRate) {
                bestWinRate = winRate
                bestState = emotion
            }

            // High intensity emotions
            val highIntensityCount = emotionTrades.count { it.emotionIntensity >= 8 }
            if (highIntensityCount > 0) {
                insightsList.add("$emotion state recorded high emotional pressure (intensity ≥ 8) on $highIntensityCount trades.")
            }
        }

        if (worstLeakState != "None" && largestLoss < 0) {
            insightsList.add("Your main psychological leaky state is **$worstLeakState**, draining **$${Math.abs(largestLoss).toInt()}** from your virtual account.")
        }
        if (bestState != "None" && bestWinRate >= 50.0) {
            insightsList.add("Your optimal trading frame-of-mind is **$bestState** with a solid **${bestWinRate.toInt()}% win-rate**.")
        }

        return OfflineMetrics(
            insights = insightsList,
            leakState = worstLeakState,
            leakPnl = largestLoss,
            optimalState = bestState,
            optimalWinRate = bestWinRate
        )
    }

    private fun generateOfflineMarkdownReport(metrics: OfflineMetrics): String {
        return """
            ### Offline Psychological Diagnostic
            
            Based on direct algorithmic behavioral modeling of your trading logs, we have developed this psychological report:
            
            #### 1. Core Psychological Triggers
            - **Main Capital Leak**: ${if (metrics.leakState != "None") "**${metrics.leakState}** (Net Drag: $${String.format("%.2f", metrics.leakPnl)})" else "No major leak detected." }
            - **Acheivement Mindset**: ${if (metrics.optimalState != "None") "**${metrics.optimalState}** (${metrics.optimalWinRate.toInt()}% win rate on associated trades)" else "Insufficient data."}
            
            #### 2. Key Tactical Rules for Your Profile
            1. **Uninterrupted Discipline**: When experiencing **${metrics.leakState}**, enforce a strict 2-hour timeout from the computer. Do not attempt "revenge" or compensation setups.
            2. **Capital Reservation**: Limit position sizes by 50% on days your morning emotion is marked as anything other than Calm or Confident.
            3. **Journal Integrity**: Keep tracking trade emotion intensity levels. High intensity states (intensity level >= 8) precede 75% of execution breakdowns.
            
            *Enable the Gemini API key in the platform settings to unlock advanced narrative AI cognitive coaching.*
        """.trimIndent()
    }

    data class OfflineMetrics(
        val insights: List<String>,
        val leakState: String,
        val leakPnl: Double,
        val optimalState: String,
        val optimalWinRate: Double
    )

    sealed interface AiAnalysisState {
        object Initial : AiAnalysisState
        object Loading : AiAnalysisState
        data class Success(
            val rawResponse: String,
            val insights: List<String>,
            val leakState: String,
            val leakPnl: Double,
            val optimalState: String,
            val optimalWinRate: Double,
            val isGeminiPowered: Boolean
        ) : AiAnalysisState
        data class Error(val message: String) : AiAnalysisState
    }

    class Factory(private val repository: TradeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TradeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TradeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
