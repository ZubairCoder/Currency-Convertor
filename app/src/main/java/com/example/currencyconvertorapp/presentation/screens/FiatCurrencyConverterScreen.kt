package com.example.currencyconvertorapp.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.currencyconvertorapp.data.model.CurrencyInfo
import com.example.currencyconvertorapp.presentation.viewmodel.CurrencyViewModel
import com.example.currencyconvertorapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiatCurrencyConverterScreen(
    navController: NavController,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val supportedCurrencies by viewModel.supportedCurrencies.collectAsState()
    val prices by viewModel.prices.collectAsState()
    
    var baseAmount by remember { mutableStateOf("1.0") }
    var baseCurrency by remember { mutableStateOf("USD") }
    var isLoading by remember { mutableStateOf(true) }
    
    // Initialize with popular fiat currencies only
    var selectedTargetCurrencies by remember { 
        mutableStateOf(
            setOf("EUR", "GBP", "PKR") // Start with some popular currencies for better UX
        ) 
    }
    
    var showCurrencyDialog by remember { mutableStateOf(false) }
    
    // Filter to show only fiat currencies
    val fiatCurrencies = remember(supportedCurrencies) {
        supportedCurrencies.filter { currency ->
            currency.code in setOf(
                "USD", "EUR", "GBP", "PKR", "AED", "MYR", "SAR", "QAR", 
                "KWD", "BHD", "OMR", "INR", "JPY", "CNY", "KRW", "AUD", 
                "CAD", "CHF", "TRY", "RUB", "BRL", "ZAR", "NGN", "EGP", "PHP"
            )
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadAllPrices()
        // Also ensure supported currencies are loaded
        viewModel.reloadSupportedCurrencies()
    }
    
    LaunchedEffect(supportedCurrencies) {
        if (supportedCurrencies.isNotEmpty()) {
            isLoading = false
        }
    }
    
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
    ) {
        // Enhanced Top App Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fiat Currency Converter",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Convert between world currencies",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CurrencyGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CurrencyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Enhanced Amount Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Amount to Convert",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = baseAmount,
                        onValueChange = { newValue ->
                            // Only allow valid decimal numbers
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                baseAmount = newValue
                            }
                        },
                        label = { Text("Amount") },
                        placeholder = { Text("Enter amount to convert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Text("Enter the amount you want to convert")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Enhanced Base Currency Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Base Currency",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var expandedBaseCurrency by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedBaseCurrency,
                        onExpandedChange = { expandedBaseCurrency = !expandedBaseCurrency }
                    ) {
                        OutlinedTextField(
                            value = "$baseCurrency - ${fiatCurrencies.find { it.code == baseCurrency }?.name ?: baseCurrency}",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("From Currency") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBaseCurrency) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandedBaseCurrency,
                            onDismissRequest = { expandedBaseCurrency = false }
                        ) {
                            fiatCurrencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { 
                                        Text("${currency.symbol} ${currency.code} - ${currency.name}")
                                    },
                                    onClick = {
                                        baseCurrency = currency.code
                                        expandedBaseCurrency = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Enhanced Target Currency Selection Section
            FiatCurrencySelectionSection(
                selectedCurrencies = selectedTargetCurrencies,
                availableCurrencies = fiatCurrencies,
                baseCurrency = baseCurrency,
                onCurrencyAdd = { currency ->
                    if (selectedTargetCurrencies.size < 8 && currency != baseCurrency) {
                        selectedTargetCurrencies = selectedTargetCurrencies + currency
                    }
                },
                onCurrencyRemove = { currency ->
                    selectedTargetCurrencies = selectedTargetCurrencies - currency
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Enhanced Conversion Results with loading state
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                if (isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading currencies...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (selectedTargetCurrencies.isNotEmpty() && baseAmount.isNotEmpty()) {
                    val amount = baseAmount.toDoubleOrNull()
                    
                    if (amount != null && amount > 0) {
                        Text(
                            text = "Conversion Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedTargetCurrencies.toList()) { targetCurrencyCode ->
                                val targetCurrency = fiatCurrencies.find { it.code == targetCurrencyCode }
                                if (targetCurrency != null) {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = slideInHorizontally() + fadeIn()
                                    ) {
                                        FiatCurrencyConversionCard(
                                            currency = targetCurrency,
                                            baseAmount = amount,
                                            baseCurrency = baseCurrency,
                                            viewModel = viewModel
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Please enter a valid amount",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                } else if (selectedTargetCurrencies.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No target currencies selected",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add currencies to see conversion results",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Please enter an amount",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiatCurrencySelectionSection(
    selectedCurrencies: Set<String>,
    availableCurrencies: List<CurrencyInfo>,
    baseCurrency: String,
    onCurrencyAdd: (String) -> Unit,
    onCurrencyRemove: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                    text = "Target Currencies (${selectedCurrencies.size}/8)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                FilledTonalButton(
                    onClick = { showDialog = true },
                    enabled = selectedCurrencies.size < 8,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Currency",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (selectedCurrencies.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(selectedCurrencies.toList()) { currencyCode ->
                        val currency = availableCurrencies.find { it.code == currencyCode }
                        if (currency != null) {
                            FiatSelectedCurrencyChip(
                                currency = currency,
                                onRemove = { onCurrencyRemove(currencyCode) }
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No currencies selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap 'Add' to select currencies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        FiatCurrencySelectionDialog(
            availableCurrencies = availableCurrencies.filter { 
                it.code != baseCurrency && it.code !in selectedCurrencies 
            },
            selectedCurrencies = selectedCurrencies,
            baseCurrency = baseCurrency,
            onCurrencySelected = { currency ->
                onCurrencyAdd(currency.code)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun FiatSelectedCurrencyChip(
    currency: CurrencyInfo,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currency.symbol,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currency.name.take(12) + if (currency.name.length > 12) "..." else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${currency.code}",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiatCurrencySelectionDialog(
    availableCurrencies: List<CurrencyInfo>,
    selectedCurrencies: Set<String>,
    baseCurrency: String,
    onCurrencySelected: (CurrencyInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredCurrencies = remember(availableCurrencies, searchQuery) {
        if (searchQuery.isBlank()) {
            availableCurrencies
        } else {
            availableCurrencies.filter { currency ->
                currency.name.contains(searchQuery, ignoreCase = true) ||
                currency.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Select Currency",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose a currency to add",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search currencies") },
                    placeholder = { Text("Type currency name or code") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredCurrencies) { currency ->
                        val isSelected = currency.code in selectedCurrencies
                        val isBaseCurrency = currency.code == baseCurrency
                        val isDisabled = isSelected || isBaseCurrency
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { 
                                if (!isDisabled) {
                                    onCurrencySelected(currency)
                                }
                            },
                            enabled = !isDisabled,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDisabled) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currency.symbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currency.code,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currency.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                if (isSelected) {
                                    Text(
                                        text = "Selected",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else if (isBaseCurrency) {
                                    Text(
                                        text = "Base",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add ${currency.code}",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                if (filteredCurrencies.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No currencies found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try a different search term",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}

@Composable
fun FiatCurrencyConversionCard(
    currency: CurrencyInfo,
    baseAmount: Double,
    baseCurrency: String,
    viewModel: CurrencyViewModel
) {
    val convertedAmount = remember(baseAmount, baseCurrency, currency.code) {
        viewModel.convertCurrency(baseAmount, baseCurrency, currency.code)
    }
    
    val conversionRate = remember(baseCurrency, currency.code) {
        if (baseAmount > 0) {
            convertedAmount / baseAmount
        } else {
            viewModel.convertCurrency(1.0, baseCurrency, currency.code)
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currency.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = currency.code,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currency.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${currency.symbol} ${String.format("%.3f", convertedAmount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "≈ ${String.format("%.4f", conversionRate)} per $baseCurrency",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Conversion rate indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1 $baseCurrency = ${String.format("%.4f", conversionRate)} ${currency.code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiatCurrencyConverterScreenPreview() {
    FiatCurrencyConverterScreen(rememberNavController())
}
