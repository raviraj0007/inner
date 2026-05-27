package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.ServiceLocator
import com.example.ui.screens.AddTradeScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TradeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Core Offline-First ServiceLocator
        ServiceLocator.initialize(applicationContext)
        
        val viewModel = ViewModelProvider(
            this,
            TradeViewModel.Factory(ServiceLocator.tradeRepository!!)
        )[TradeViewModel::class.java]

        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val trades by viewModel.allTrades.collectAsState()
                val analysisState by viewModel.aiAnalysisState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                trades = trades,
                                onAddTradeClick = { navController.navigate("add_trade") },
                                onAiCoachClick = { 
                                    viewModel.triggerAiPsychologyAnalysis()
                                    navController.navigate("ai_coach") 
                                },
                                onDeleteTrade = { trade -> viewModel.deleteTrade(trade) }
                            )
                        }
                        composable("add_trade") {
                            AddTradeScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onSaveTrade = { asset, orderType, entryPrice, exitPrice, quantity, emotion, emotionIntensity, notes ->
                                    viewModel.addTrade(
                                        asset = asset,
                                        orderType = orderType,
                                        entryPrice = entryPrice,
                                        exitPrice = exitPrice,
                                        quantity = quantity,
                                        emotion = emotion,
                                        emotionIntensity = emotionIntensity,
                                        notes = notes
                                    )
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("ai_coach") {
                            AiCoachScreen(
                                analysisState = analysisState,
                                onTriggerAnalysis = { viewModel.triggerAiPsychologyAnalysis() },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
