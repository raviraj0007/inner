package com.example.data.repository

import com.example.data.database.TradeDao
import com.example.data.database.TradeEntity
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    fun getAllTrades(): Flow<List<TradeEntity>>
    suspend fun insertTrade(trade: TradeEntity): Long
    suspend fun deleteTrade(trade: TradeEntity)
    suspend fun getTradeById(id: Long): TradeEntity?
    suspend fun getTradesByEmotion(emotion: String): List<TradeEntity>
}

class TradeRepositoryImpl(
    private val tradeDao: TradeDao
) : TradeRepository {
    override fun getAllTrades(): Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    override suspend fun insertTrade(trade: TradeEntity): Long = tradeDao.insertTrade(trade)

    override suspend fun deleteTrade(trade: TradeEntity) = tradeDao.deleteTrade(trade)

    override suspend fun getTradeById(id: Long): TradeEntity? = tradeDao.getTradeById(id)

    override suspend fun getTradesByEmotion(emotion: String): List<TradeEntity> = tradeDao.getTradesByEmotion(emotion)
}
