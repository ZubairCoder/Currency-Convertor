package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateAdjustmentScreen() {
    var adjustmentPercentage by remember { mutableStateOf("0.0") }
    
    val sampleRates = remember {
        listOf(
            "BTCUSDT" to 45000.0,
            "ETHUSDT" to 3000.0,
            "BNBUSDT" to 400.0,
            "ADAUSDT" to 1.2,
            "XRPUSDT" to 0.6
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Exchange Rate Adjustment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        OutlinedTextField(
            value = adjustmentPercentage,
            onValueChange = { adjustmentPercentage = it },
            label = { Text("Adjustment Percentage (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        Button(
            onClick = { /* TODO: Apply adjustment */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Adjustment")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Current Rates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn {
            items(sampleRates) { (symbol, rate) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%.2f", rate)}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            val adjustedRate = rate * (1 + (adjustmentPercentage.toDoubleOrNull() ?: 0.0) / 100)
                            Text(
                                text = "Adjusted: $${String.format("%.2f", adjustedRate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}