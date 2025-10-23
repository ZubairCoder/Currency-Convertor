package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySimulationScreen() {
    var initialAmount by remember { mutableStateOf("1000") }
    var targetAmount by remember { mutableStateOf("1200") }
    var timeFrame by remember { mutableStateOf("30") }
    var selectedCurrency by remember { mutableStateOf("BTCUSDT") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Currency Simulation",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        OutlinedTextField(
            value = initialAmount,
            onValueChange = { initialAmount = it },
            label = { Text("Initial Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = targetAmount,
            onValueChange = { targetAmount = it },
            label = { Text("Target Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = timeFrame,
            onValueChange = { timeFrame = it },
            label = { Text("Time Frame (days)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        Button(
            onClick = { /* TODO: Implement simulation logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Simulation")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Simulation Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Initial: $${initialAmount}")
                Text("Target: $${targetAmount}")
                Text("Time Frame: ${timeFrame} days")
                Text("Expected Growth: ${((targetAmount.toDoubleOrNull() ?: 0.0) - (initialAmount.toDoubleOrNull() ?: 0.0))} USD")
            }
        }
    }
}