package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val asset: String,
    val orderType: String, // "BUY" or "SELL"
    val entryPrice: Double,
    val exitPrice: Double,
    val quantity: Double,
    val timestamp: Long,
    val pnl: Double,
    val emotion: String, // e.g. "Calm", "Fear", "Greed", "Impatient", "FOMO", "Confident"
    val emotionIntensity: Int, // 1 to 10
    val notes: String,
    val isWin: Boolean
)
