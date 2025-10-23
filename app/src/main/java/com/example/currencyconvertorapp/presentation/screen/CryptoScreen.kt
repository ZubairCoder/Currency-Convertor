package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertorapp.data.model.CryptoInfo
import com.example.currencyconvertorapp.presentation.viewmodel.CryptoViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoScreen(
    viewModel: CryptoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Cryptocurrency Prices")
                        if (uiState.isConnected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Live",
                                tint = Color.Green,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Green
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Connection Status
            if (!uiState.isConnected && !uiState.isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠️ Connection Lost - Showing last known prices",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Loading State
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading crypto prices...")
                    }
                }
            }

            // Error State
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.refreshData() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            // Crypto List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.cryptos) { crypto ->
                    CryptoCard(crypto = crypto)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoCard(
    crypto: CryptoInfo,
    modifier: Modifier = Modifier
) {
    val isPositive = crypto.priceChangePercent >= 0
    val changeColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
    
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val percentFormatter = NumberFormat.getPercentInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with name, symbol and rank
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${getCryptoRank(crypto.symbol)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = crypto.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = crypto.symbol.replace("USDT", "/USD"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Price change indicator
                Surface(
                    color = changeColor,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${percentFormatter.format(crypto.priceChangePercent / 100)}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Current Price (Large)
            Text(
                text = currencyFormatter.format(crypto.currentPrice),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats Grid (2x3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column
                Column(modifier = Modifier.weight(1f)) {
                    StatItem("24h High", crypto.high24h?.let { currencyFormatter.format(it) } ?: "N/A")
                    Spacer(modifier = Modifier.height(8.dp))
                    StatItem("24h Low", crypto.low24h?.let { currencyFormatter.format(it) } ?: "N/A")
                    Spacer(modifier = Modifier.height(8.dp))
                    StatItem("Volume", formatVolume(crypto.volume24h))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Right Column
                Column(modifier = Modifier.weight(1f)) {
                    StatItem("Market Cap", formatMarketCap(calculateMarketCap(crypto)))
                    Spacer(modifier = Modifier.height(8.dp))
                    StatItem("Change (24h)", "${if (isPositive) "+" else ""}${String.format("%.2f", crypto.priceChangePercent)}%")
                    Spacer(modifier = Modifier.height(8.dp))
                    StatItem("Status", "Live")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatMarketCap(marketCap: Double): String {
    return when {
        marketCap >= 1_000_000_000_000 -> "${String.format("%.1f", marketCap / 1_000_000_000_000)}T"
        marketCap >= 1_000_000_000 -> "${String.format("%.1f", marketCap / 1_000_000_000)}B"
        marketCap >= 1_000_000 -> "${String.format("%.1f", marketCap / 1_000_000)}M"
        else -> "${String.format("%.0f", marketCap)}"
    }
}

private fun calculateMarketCap(crypto: CryptoInfo): Double {
    // Simplified market cap calculation based on volume and price
    return crypto.volume24h * crypto.currentPrice * 365
}

private fun getCryptoRank(symbol: String): Int {
    // Simplified ranking based on market cap order
    val rankings = mapOf(
        "BTCUSDT" to 1, "ETHUSDT" to 2, "BNBUSDT" to 3, "XRPUSDT" to 4, "ADAUSDT" to 5,
        "SOLUSDT" to 6, "DOGEUSDT" to 7, "TRXUSDT" to 8, "TONUSDT" to 9, "AVAXUSDT" to 10,
        "DOTUSDT" to 11, "MATICUSDT" to 12, "LINKUSDT" to 13, "UNIUSDT" to 14, "LTCUSDT" to 15,
        "BCHUSDT" to 16, "NEARUSDT" to 17, "ATOMUSDT" to 18, "FILUSDT" to 19, "VETUSDT" to 20
    )
    return rankings[symbol] ?: 999
}

private fun formatVolume(volume: Double): String {
    return when {
        volume >= 1_000_000_000 -> "${String.format("%.1f", volume / 1_000_000_000)}B"
        volume >= 1_000_000 -> "${String.format("%.1f", volume / 1_000_000)}M"
        volume >= 1_000 -> "${String.format("%.1f", volume / 1_000)}K"
        else -> String.format("%.0f", volume)
    }
}