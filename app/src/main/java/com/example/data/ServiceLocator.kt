package com.example.data

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.repository.TradeRepository
import com.example.data.repository.TradeRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

object ServiceLocator {
    private var database: AppDatabase? = null
    var tradeRepository: TradeRepository? = null
        private set

    fun initialize(context: Context) {
        if (tradeRepository != null) return
        val applicationScope = CoroutineScope(SupervisorJob())
        val db = AppDatabase.getDatabase(context.applicationContext, applicationScope)
        database = db
        tradeRepository = TradeRepositoryImpl(db.tradeDao())
    }
}
