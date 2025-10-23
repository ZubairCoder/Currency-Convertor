package com.example.currencyconvertorapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRealTime: () -> Unit,
    onNavigateToMultiCurrency: () -> Unit,
    onNavigateToMulti8Currency: () -> Unit,
    onNavigateToTrendCharts: () -> Unit,
    onNavigateToExchangeRateList: () -> Unit,
    onNavigateToCurrencySimulation: () -> Unit,
    onNavigateToExchangeRateAdjustment: () -> Unit,
    onNavigateToCurrencyProfile: () -> Unit
) {
    val menuItems = listOf(
        MenuItem("Real-Time Currency", Icons.Default.Menu, onNavigateToRealTime),
        MenuItem("Multi Currency Converter", Icons.Default.Build, onNavigateToMultiCurrency),
        MenuItem("Multi 8 Currency Converter", Icons.Default.Person, onNavigateToMulti8Currency),
        MenuItem("Trend Charts", Icons.Default.AccountBox, onNavigateToTrendCharts),
        MenuItem("Exchange Rate List", Icons.Default.List, onNavigateToExchangeRateList),
        MenuItem("Currency Simulation", Icons.Default.PlayArrow, onNavigateToCurrencySimulation),
        MenuItem("Exchange Rate Adjustment", Icons.Default.Settings, onNavigateToExchangeRateAdjustment),
        MenuItem("Currency Profile", Icons.Default.Person, onNavigateToCurrencyProfile)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                MenuItemCard(
                    title = item.title,
                    icon = item.icon,
                    onClick = item.onClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)