package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BearishRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTradeScreen(
    onNavigateBack: () -> Unit,
    onSaveTrade: (
        asset: String,
        orderType: String,
        entryPrice: Double,
        exitPrice: Double,
        quantity: Double,
        emotion: String,
        emotionIntensity: Int,
        notes: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local Inputs
    var asset by remember { mutableStateOf("") }
    var orderType by remember { mutableStateOf("BUY") } // BUY or SELL
    var entryPrice by remember { mutableStateOf("") }
    var exitPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1.0") }
    var selectedEmotion by remember { mutableStateOf("Calm") }
    var emotionIntensity by remember { mutableStateOf(5f) }
    var notes by remember { mutableStateOf("") }

    // Validation Status
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val emotionsList = listOf(
        "Calm", "Confident", "FOMO", "Greedy", "Fearful", "Impatient", "Anxious", "Revenge"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Trade Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Order Type Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (orderType == "BUY") BullishGreen.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { orderType = "BUY" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Long (BUY)",
                        fontWeight = FontWeight.Bold,
                        color = if (orderType == "BUY") BullishGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (orderType == "SELL") BearishRed.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { orderType = "SELL" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Short (SELL)",
                        fontWeight = FontWeight.Bold,
                        color = if (orderType == "SELL") BearishRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            // Asset Name Text Field
            TextField(
                value = asset,
                onValueChange = { asset = it },
                label = { Text("Symbol Name (e.g. BTC/USDT)") },
                placeholder = { Text("Enter asset ticker") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("asset_input"),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Dynamic Numeric Grid Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = entryPrice,
                    onValueChange = { entryPrice = it },
                    label = { Text("Entry Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                TextField(
                    value = exitPrice,
                    onValueChange = { exitPrice = it },
                    label = { Text("Exit Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Volume/Quantity Input
            TextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Position Quantity / Volume") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))

            // State & Mindset selector
            Column {
                Text(
                    text = "Current Emotional Mindset",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select the primary feeling that dictated this execution.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Beautiful wide grid of emotional targets using compiled-safe nested Rows
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emotionsList.chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (emotion in chunk) {
                                val isSelected = selectedEmotion == emotion
                                val activeColor = when (emotion.lowercase()) {
                                    "calm" -> MaterialTheme.colorScheme.tertiary
                                    "confident" -> MaterialTheme.colorScheme.primary
                                    "fomo", "greedy" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.error
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) activeColor.copy(alpha = 0.2f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .clickable { selectedEmotion = emotion }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${emotionEmoji(emotion)} $emotion",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            // Filler to maintain equal spacing when a row isn't full
                            if (chunk.size < 3) {
                                repeat(3 - chunk.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Emotion Intensity Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Emotion Intensity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${emotionIntensity.toInt()} / 10",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = emotionIntensity,
                    onValueChange = { emotionIntensity = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.05f)))

            // Behavioral Journal Setup Notes
            TextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Context & Self-Correction Notes") },
                placeholder = { Text("What did you feel? Was this a chase? What was your technical trigger?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("notes_input"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Error Callout if invalid
            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Save CTA Button
            Button(
                onClick = {
                    val entryVal = entryPrice.toDoubleOrNull()
                    val exitVal = exitPrice.toDoubleOrNull()
                    val qtyVal = quantity.toDoubleOrNull()

                    if (asset.isBlank()) {
                        errorMessage = "Please enter an asset symbol name."
                        showError = true
                    } else if (entryVal == null || entryVal <= 0) {
                        errorMessage = "Please enter a valid, positive entry price."
                        showError = true
                    } else if (exitVal == null || exitVal <= 0) {
                        errorMessage = "Please enter a valid, positive exit price."
                        showError = true
                    } else if (qtyVal == null || qtyVal <= 0) {
                        errorMessage = "Please enter a valid, positive position quantity."
                        showError = true
                    } else {
                        showError = false
                        onSaveTrade(
                            asset,
                            orderType,
                            entryVal,
                            exitVal,
                            qtyVal,
                            selectedEmotion,
                            emotionIntensity.toInt(),
                            notes
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_trade_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Lock Trade into Database", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
