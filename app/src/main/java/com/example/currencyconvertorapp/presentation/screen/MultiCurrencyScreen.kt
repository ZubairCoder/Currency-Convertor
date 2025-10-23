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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconvertorapp.presentation.viewmodel.MultiCurrencyViewModel
import com.example.currencyconvertorapp.presentation.viewmodel.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCurrencyScreen(
    viewModel: MultiCurrencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var amountText by remember { mutableStateOf("1.0") }
    var selectedCurrency by remember { mutableStateOf("BTC") }
    
    val supportedCurrencies by hiltViewModel<CurrencyViewModel>().supportedCurrencies.collectAsState()
    val currencies = supportedCurrencies.map { it.code }
    val targetCurrencies = listOf("USDT", "BUSD", "EUR", "GBP", "JPY")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Multi Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Amount Input
        OutlinedTextField(
            value = amountText,
            onValueChange = { 
                amountText = it
                viewModel.updateAmount(it)
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Currency Selection
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("From Currency") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                supportedCurrencies.forEach { currency ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (currency.symbol.isNotEmpty() && currency.symbol != currency.code) {
                                    "${currency.symbol} ${currency.code} - ${currency.name}"
                                } else {
                                    "${currency.code} - ${currency.name}"
                                }
                            )
                        },
                        onClick = {
                            selectedCurrency = currency.code
                            viewModel.updateFromCurrency(currency.code)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: 1.0
                viewModel.convertCurrency(amount, selectedCurrency, targetCurrencies)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
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
            Text(
                text = "Conversion Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.conversions.toList()) { (currency, value) ->
                    ConversionResultCard(
                        fromAmount = uiState.amount,
                        fromCurrency = uiState.fromCurrency,
                        toCurrency = currency,
                        toAmount = value
                    )
                }
            }
        }
    }
}

@Composable
fun ConversionResultCard(
    fromAmount: Double,
    fromCurrency: String,
    toCurrency: String,
    toAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$fromAmount $fromCurrency",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "=",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "${String.format("%.6f", toAmount)} $toCurrency",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}