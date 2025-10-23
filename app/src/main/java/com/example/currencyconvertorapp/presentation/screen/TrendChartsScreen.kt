package com.example.currencyconvertorapp.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertorapp.presentation.viewmodel.TrendChartsViewModel
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendChartsScreen(
    viewModel: TrendChartsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val symbols = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "ADAUSDT", "XRPUSDT")
    val intervals = listOf("1m", "5m", "15m", "1h", "4h", "1d")
    
    var selectedSymbol by remember { mutableStateOf("BTCUSDT") }
    var selectedInterval by remember { mutableStateOf("1h") }
    
    LaunchedEffect(selectedSymbol, selectedInterval) {
        viewModel.loadChartData(selectedSymbol, selectedInterval)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Trend Charts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Symbol Selection
        var symbolExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = symbolExpanded,
            onExpandedChange = { symbolExpanded = !symbolExpanded }
        ) {
            OutlinedTextField(
                value = selectedSymbol,
                onValueChange = {},
                readOnly = true,
                label = { Text("Trading Pair") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = symbolExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = symbolExpanded,
                onDismissRequest = { symbolExpanded = false }
            ) {
                symbols.forEach { symbol ->
                    DropdownMenuItem(
                        text = { Text(symbol) },
                        onClick = {
                            selectedSymbol = symbol
                            viewModel.updateSymbol(symbol)
                            symbolExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Interval Selection
        var intervalExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = intervalExpanded,
            onExpandedChange = { intervalExpanded = !intervalExpanded }
        ) {
            OutlinedTextField(
                value = selectedInterval,
                onValueChange = {},
                readOnly = true,
                label = { Text("Time Interval") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = intervalExpanded,
                onDismissRequest = { intervalExpanded = false }
            ) {
                intervals.forEach { interval ->
                    DropdownMenuItem(
                        text = { Text(interval) },
                        onClick = {
                            selectedInterval = interval
                            viewModel.updateInterval(interval)
                            intervalExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else if (uiState.chartData.isNotEmpty()) {
            // Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LineChart(
                    data = uiState.chartData.map { it.close },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart Statistics
            if (uiState.chartData.isNotEmpty()) {
                val currentPrice = uiState.chartData.last().close
                val previousPrice = uiState.chartData.first().close
                val priceChange = currentPrice - previousPrice
                val priceChangePercent = (priceChange / previousPrice) * 100
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Current Price",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$${String.format("%.2f", currentPrice)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Column {
                                Text(
                                    text = "Change",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val changeColor = if (priceChange >= 0) Color.Green else Color.Red
                                Text(
                                    text = "${if (priceChange >= 0) "+" else ""}${String.format("%.2f", priceChangePercent)}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = changeColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<Double>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val padding = 40f
        
        val minValue = data.minOrNull() ?: 0.0
        val maxValue = data.maxOrNull() ?: 1.0
        val range = maxValue - minValue
        
        if (range == 0.0) return@Canvas
        
        val path = Path()
        val stepX = (width - 2 * padding) / (data.size - 1)
        
        data.forEachIndexed { index, value ->
            val x = padding + index * stepX
            val y = height - padding - ((value - minValue) / range * (height - 2 * padding)).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3f)
        )
        
        // Draw data points
        data.forEachIndexed { index, value ->
            val x = padding + index * stepX
            val y = height - padding - ((value - minValue) / range * (height - 2 * padding)).toFloat()
            
            drawCircle(
                color = Color.Blue,
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}