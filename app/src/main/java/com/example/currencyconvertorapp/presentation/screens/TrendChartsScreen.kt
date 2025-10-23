package com.example.currencyconvertorapp.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertorapp.presentation.viewmodel.TrendChartsViewModel
import com.example.currencyconvertorapp.ui.theme.CurrencyConvertorAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendChartsScreen(
    navController: NavController,
    viewModel: TrendChartsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCurrency by remember { mutableStateOf("BTCUSDT") }
    var selectedInterval by remember { mutableStateOf("1h") }
    var expanded by remember { mutableStateOf(false) }
    var intervalExpanded by remember { mutableStateOf(false) }
    
    val currencies = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "ADAUSDT", "XRPUSDT", "DOTUSDT")
    val intervals = listOf("1m", "5m", "15m", "30m", "1h", "4h", "1d", "1w")
    
    LaunchedEffect(selectedCurrency, selectedInterval) {
        viewModel.loadChartData(selectedCurrency, selectedInterval)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trend Charts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Currency and Interval Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Currency Selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCurrency,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        label = { Text("Currency Pair") }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency) },
                                onClick = {
                                    selectedCurrency = currency
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Interval Selector
                ExposedDropdownMenuBox(
                    expanded = intervalExpanded,
                    onExpandedChange = { intervalExpanded = !intervalExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedInterval,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalExpanded) },
                        modifier = Modifier.menuAnchor(),
                        label = { Text("Interval") }
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
                                    intervalExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (uiState.error != null) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (uiState.chartData.isNotEmpty()) {
                        CandlestickChart(
                            data = uiState.chartData,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "No data available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart Info
            if (uiState.chartData.isNotEmpty()) {
                val latestData = uiState.chartData.last()
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Latest Price Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Open: ${"%.6f".format(latestData.open)}")
                                Text("High: ${"%.6f".format(latestData.high)}")
                            }
                            Column {
                                Text("Low: ${"%.6f".format(latestData.low)}")
                                Text("Close: ${"%.6f".format(latestData.close)}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrendChartsScreenPreview() {
    CurrencyConvertorAppTheme {
        TrendChartsScreen(navController = rememberNavController())
    }
}

@Composable
fun CandlestickChart(
    data: List<com.example.currencyconvertorapp.presentation.viewmodel.ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val padding = 40f
        
        val minPrice = data.minOf { minOf(it.low, it.high, it.open, it.close) }
        val maxPrice = data.maxOf { maxOf(it.low, it.high, it.open, it.close) }
        val priceRange = maxPrice - minPrice
        
        val candleWidth = (width - 2 * padding) / data.size
        
        data.forEachIndexed { index, candle ->
            val x = padding + index * candleWidth + candleWidth / 2
            
            val openY = height - padding - ((candle.open - minPrice) / priceRange) * (height - 2 * padding)
            val closeY = height - padding - ((candle.close - minPrice) / priceRange) * (height - 2 * padding)
            val highY = height - padding - ((candle.high - minPrice) / priceRange) * (height - 2 * padding)
            val lowY = height - padding - ((candle.low - minPrice) / priceRange) * (height - 2 * padding)
            
            val color = if (candle.close >= candle.open) Color.Green else Color.Red
            
            // Draw high-low line
            drawLine(
                color = color,
                start = Offset(x, highY.toFloat()),
                end = Offset(x, lowY.toFloat()),
                strokeWidth = 2f
            )
            
            // Draw open-close rectangle
            val rectTop = minOf(openY, closeY).toFloat()
            val rectBottom = maxOf(openY, closeY).toFloat()
            val rectLeft = x - candleWidth / 4
            val rectRight = x + candleWidth / 4
            
            drawRect(
                color = color,
                topLeft = Offset(rectLeft, rectTop),
                size = androidx.compose.ui.geometry.Size(
                    rectRight - rectLeft,
                    rectBottom - rectTop
                )
            )
        }
    }
}