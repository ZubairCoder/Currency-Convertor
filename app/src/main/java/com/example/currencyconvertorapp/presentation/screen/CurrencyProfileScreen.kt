package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CurrencyProfileScreen() {
    val currencies = listOf(
        CurrencyProfile("Bitcoin", "BTC", "The first cryptocurrency", "$45,000"),
        CurrencyProfile("Ethereum", "ETH", "Smart contract platform", "$3,200"),
        CurrencyProfile("Binance Coin", "BNB", "Exchange token", "$320"),
        CurrencyProfile("Cardano", "ADA", "Proof-of-stake blockchain", "$0.45"),
        CurrencyProfile("XRP", "XRP", "Digital payment protocol", "$0.52")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Currency Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currencies) { currency ->
                CurrencyProfileCard(currency = currency)
            }
        }
    }
}

@Composable
fun CurrencyProfileCard(currency: CurrencyProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currency.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currency.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Current Price: ${currency.currentPrice}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class CurrencyProfile(
    val name: String,
    val symbol: String,
    val description: String,
    val currentPrice: String
)