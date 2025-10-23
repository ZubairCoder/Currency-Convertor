package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertorapp.presentation.viewmodel.MultiCurrencyViewModel

@Composable
fun Multi8CurrencyScreen(
    viewModel: MultiCurrencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("1.0") }
    
    val currencies = listOf("BTC", "ETH", "BNB", "ADA", "XRP", "DOT", "LINK", "LTC")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Multi 8 Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = amountText,
            onValueChange = { 
                amountText = it
                viewModel.updateAmount(it)
            },
            label = { Text("Amount in USD") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: 1.0
                viewModel.convertCurrency(amount, "USDT", currencies)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert to 8 Currencies")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.conversions.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.conversions.toList()) { (currency, value) ->
                    CurrencyGridCard(
                        currency = currency,
                        amount = value,
                        baseAmount = uiState.amount
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyGridCard(
    currency: String,
    amount: Double,
    baseAmount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currency,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = String.format("%.6f", amount),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "≈ $${String.format("%.2f", baseAmount)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}