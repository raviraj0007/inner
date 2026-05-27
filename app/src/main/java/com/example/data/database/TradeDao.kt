package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity): Long

    @Delete
    suspend fun deleteTrade(trade: TradeEntity)

    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: Long): TradeEntity?

    @Query("SELECT * FROM trades WHERE emotion = :emotion")
    suspend fun getTradesByEmotion(emotion: String): List<TradeEntity>

    @Query("DELETE FROM trades")
    suspend fun clearAll()
}
