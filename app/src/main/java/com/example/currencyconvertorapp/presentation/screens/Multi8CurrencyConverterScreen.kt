package com.example.currencyconvertorapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertorapp.data.model.CurrencyInfo
import com.example.currencyconvertorapp.presentation.viewmodel.CurrencyViewModel
import com.example.currencyconvertorapp.ui.theme.CurrencyConvertorAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Multi8CurrencyConverterScreen(
    navController: NavController,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    var baseAmount by remember { mutableStateOf("1.0") }
    var baseCurrency by remember { mutableStateOf("USDT") }
    var expanded by remember { mutableStateOf(false) }

    val supportedCurrencies by viewModel.supportedCurrencies.collectAsState()

    // User-selected target currencies (initially popular fiat currencies)
    var selectedTargetCurrencies by remember {
        mutableStateOf(
            setOf("PKR", "AED", "MYR", "EUR", "GBP", "INR", "SAR", "USD")
        )
    }

//    val exchangeRates by viewModel.exchangeRates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExchangeRates()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi 8 Currency Converter") },
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
            // Base Currency Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Base Amount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = baseAmount,
                            onValueChange = { baseAmount = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = baseCurrency,
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor(),
                                label = { Text("Currency") }
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
                                            baseCurrency = currency.code
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currency Selection Section
            CurrencySelectionSection(
                selectedCurrencies = selectedTargetCurrencies,
                availableCurrencies = supportedCurrencies,
                onCurrenciesChanged = { newSelection ->
                    selectedTargetCurrencies = newSelection
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Conversion Results
            if (selectedTargetCurrencies.isNotEmpty()) {
                Text(
                    text = "Conversion Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedTargetCurrencies.toList()) { targetCurrencyCode ->
                        val targetCurrency = supportedCurrencies.find { it.code == targetCurrencyCode }
                        if (targetCurrency != null && targetCurrency.code != baseCurrency) {
                            CurrencyConversionCard(
                                currency = targetCurrency,
                                baseAmount = baseAmount.toDoubleOrNull() ?: 0.0,
                                baseCurrency = baseCurrency,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select currencies to see conversion results",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Multi8CurrencyConverterScreenPreview() {
    CurrencyConvertorAppTheme {
        Multi8CurrencyConverterScreen(navController = rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionSection(
    selectedCurrencies: Set<String>,
    availableCurrencies: List<CurrencyInfo>,
    onCurrenciesChanged: (Set<String>) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target Currencies",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                FilledTonalButton(
                    onClick = { showAddDialog = true },
                    enabled = selectedCurrencies.size < 8
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedCurrencies.isEmpty()) {
                Text(
                    text = "No currencies selected. Tap 'Add' to choose currencies.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedCurrencies.toList()) { currencyCode ->
                        val currency = availableCurrencies.find { it.code == currencyCode }
                        if (currency != null) {
                            SelectedCurrencyChip(
                                currency = currency,
                                onRemove = {
                                    onCurrenciesChanged(selectedCurrencies - currencyCode)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Currency Dialog
    if (showAddDialog) {
        CurrencySelectionDialog(
            availableCurrencies = availableCurrencies.filter {
                !selectedCurrencies.contains(it.code)
            },
            onCurrencySelected = { currency ->
                if (selectedCurrencies.size < 8) {
                    onCurrenciesChanged(selectedCurrencies + currency.code)
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun SelectedCurrencyChip(
    currency: CurrencyInfo,
    onRemove: () -> Unit
) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text(
                text = currency.code,
                style = MaterialTheme.typography.labelMedium
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionDialog(
    availableCurrencies: List<CurrencyInfo>,
    onCurrencySelected: (CurrencyInfo) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp)
            ) {
                items(availableCurrencies) { currency ->
                    Card(
                        onClick = { onCurrencySelected(currency) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = currency.code,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (currency.symbol.isNotEmpty() && currency.symbol != currency.code) {
                                Text(
                                    text = currency.symbol,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CurrencyConversionCard(
    currency: CurrencyInfo,
    baseAmount: Double,
    baseCurrency: String,
    viewModel: CurrencyViewModel
) {
    val convertedAmount = viewModel.convertCurrency(baseAmount, baseCurrency, currency.code)

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
            Column {
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${currency.symbol} ${"%.6f".format(convertedAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "from $baseAmount $baseCurrency",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}