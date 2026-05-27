package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TradeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inneredge_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.tradeDao())
                }
            }
        }

        suspend fun populateDatabase(tradeDao: TradeDao) {
            tradeDao.clearAll()
            val now = System.currentTimeMillis()
            val hourMs = 3600_000L
            val dayMs = 24 * hourMs

            // Insert stunning mock trades representing key behavioral analysis cues
            val mockTrades = listOf(
                TradeEntity(
                    asset = "BTC/USDT",
                    orderType = "BUY",
                    entryPrice = 64500.0,
                    exitPrice = 66100.0,
                    quantity = 0.5,
                    timestamp = now - 2 * hourMs,
                    pnl = 800.0,
                    emotion = "Confident",
                    emotionIntensity = 8,
                    notes = "Riding the bullish breakout of the daily resistance. Position sizing was perfect and stayed composed throughout.",
                    isWin = true
                ),
                TradeEntity(
                    asset = "ETH/USDT",
                    orderType = "BUY",
                    entryPrice = 3450.0,
                    exitPrice = 3310.0,
                    quantity = 2.0,
                    timestamp = now - 1 * dayMs,
                    pnl = -280.0,
                    emotion = "FOMO",
                    emotionIntensity = 9,
                    notes = "Entered late after seeing a big green candle on the 5m chart. Violated my trading rules by chasing the spike, panicked when it pulled back and sold at bottom.",
                    isWin = false
                ),
                TradeEntity(
                    asset = "SOL/USDT",
                    orderType = "SELL",
                    entryPrice = 145.0,
                    exitPrice = 138.0,
                    quantity = 15.0,
                    timestamp = now - 2 * dayMs,
                    pnl = 105.0,
                    emotion = "Calm",
                    emotionIntensity = 6,
                    notes = "Trendline retest rejection on 15m. Set tight stop loss and take profit at recent support. Trade executed automatically, highly structured approach.",
                    isWin = true
                ),
                TradeEntity(
                    asset = "AAPL",
                    orderType = "BUY",
                    entryPrice = 178.5,
                    exitPrice = 181.2,
                    quantity = 10.0,
                    timestamp = now - 3 * dayMs,
                    pnl = 27.0,
                    emotion = "Impatient",
                    emotionIntensity = 7,
                    notes = "Wanted to lock in gains quickly because of chop. Exited trade prematurely before the core target was met. Left substantial profits on the table.",
                    isWin = true
                ),
                TradeEntity(
                    asset = "NVDA",
                    orderType = "BUY",
                    entryPrice = 850.0,
                    exitPrice = 810.0,
                    quantity = 4.0,
                    timestamp = now - 5 * dayMs,
                    pnl = -160.0,
                    emotion = "Fear",
                    emotionIntensity = 8,
                    notes = "Scared of a double-top structure, exited too quickly without waiting for confirmation. Emotional state heavily clouded by past losing streak.",
                    isWin = false
                ),
                TradeEntity(
                    asset = "BTC/USDT",
                    orderType = "SELL",
                    entryPrice = 67200.0,
                    exitPrice = 68500.0,
                    quantity = 0.3,
                    timestamp = now - 7 * dayMs,
                    pnl = -390.0,
                    emotion = "Greedy",
                    emotionIntensity = 9,
                    notes = "Position sizing was too large. Refused to set a hard stop loss hoping it would bounce back in my favor, resulting in a large loss.",
                    isWin = false
                )
            )

            for (trade in mockTrades) {
                tradeDao.insertTrade(trade)
            }
        }
    }
}
