package com.example.currencyconvertorapp.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertorapp.navigation.Screen
import com.example.currencyconvertorapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val menuItems = listOf(
        MenuItem(
            "Real-Time Currency",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_realtime_trend),
            CurrencyBlue
        ) { navController.navigate(Screen.REAL_TIME) },
        MenuItem(
            "Multi Currency Converter",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_multi_currency),
            CurrencyGreen
        ) { navController.navigate(Screen.MULTI_CURRENCY) },
        MenuItem(
            "Multi 8 Currency Converter",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_multi8_currency),
            CurrencyOrange
        ) { navController.navigate(Screen.MULTI_8_CURRENCY) },
        MenuItem(
            "Fiat Currency Converter",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_fiat_money),
            CurrencyPurple
        ) { navController.navigate(Screen.FIAT_CURRENCY) },
        MenuItem(
            "Trend Charts",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_trend_chart),
            CurrencyBlue
        ) { navController.navigate(Screen.TREND_CHARTS) },
        MenuItem(
            "Exchange Rate List",
            androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_exchange_list),
            CurrencyGreen
        ) { navController.navigate(Screen.EXCHANGE_RATE_LIST) },
        // Fallback to Material icons for the rest
        MenuItem(
            "Currency Simulation",
            androidx.compose.ui.graphics.vector.rememberVectorPainter(image = Icons.Default.PlayArrow),
            CurrencyOrange
        ) { navController.navigate(Screen.CURRENCY_SIMULATION) },
        MenuItem(
            "Exchange Rate Adjustment",
            androidx.compose.ui.graphics.vector.rememberVectorPainter(image = Icons.Default.Settings),
            CurrencyPurple
        ) { navController.navigate(Screen.EXCHANGE_RATE_ADJUSTMENT) },
        MenuItem(
            "Currency Profile",
            androidx.compose.ui.graphics.vector.rememberVectorPainter(image = Icons.Default.AccountCircle),
            CurrencyBlue
        ) { navController.navigate(Screen.CURRENCY_PROFILE) },
        MenuItem(
            "Cryptocurrency",
            androidx.compose.ui.graphics.vector.rememberVectorPainter(image = Icons.Default.Face),
            CurrencyGreen
        ) { navController.navigate(Screen.CRYPTO) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                CurrencyBlue.copy(alpha = 0.15f),
                                CurrencyPurple.copy(alpha = 0.15f),
                                CurrencyGreen.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.currencyconvertorapp.R.drawable.ic_currency_logo),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Currency Converter",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Your complete currency conversion solution",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            items(menuItems) { item ->
                EnhancedMenuItemCard(
                    title = item.title,
                    icon = item.icon,
                    accentColor = item.accentColor,
                    onClick = item.onClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedMenuItemCard(
    title: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap to explore",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.painter.Painter,
    val accentColor: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}