package com.example.currencyconvertorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertorapp.data.model.CurrencyInfo
import com.example.currencyconvertorapp.data.model.Ticker24hr
import com.example.currencyconvertorapp.data.model.TickerPrice
import com.example.currencyconvertorapp.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
    
    private val _prices = MutableStateFlow<List<TickerPrice>>(emptyList())
    val prices: StateFlow<List<TickerPrice>> = _prices.asStateFlow()
    
    private val _tickers = MutableStateFlow<List<Ticker24hr>>(emptyList())
    val tickers: StateFlow<List<Ticker24hr>> = _tickers.asStateFlow()
    
    private val _klines = MutableStateFlow<List<List<Any>>>(emptyList())
    val klines: StateFlow<List<List<Any>>> = _klines.asStateFlow()
    
    private val _supportedCurrencies = MutableStateFlow<List<CurrencyInfo>>(emptyList())
    val supportedCurrencies: StateFlow<List<CurrencyInfo>> = _supportedCurrencies.asStateFlow()
    
    init {
        loadAllPrices()
        load24hrTickers()
        loadSupportedCurrencies()
    }
    
    private fun privateLoadSupportedCurrencies() {
        viewModelScope.launch {
            repository.getSupportedCurrencies().collect { result ->
                result.fold(
                    onSuccess = { currencies ->
                        _supportedCurrencies.value = currencies
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load currencies: ${error.message}"
                        )
                    }
                )
            }
        }
    }
    
    // Public method to reload supported currencies
    fun reloadSupportedCurrencies() {
        loadSupportedCurrencies()
    }
    
    // Public method to reload supported currencies
    fun loadSupportedCurrencies() {
        privateLoadSupportedCurrencies()
    }
    
    fun loadAllPrices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllPrices().collect { result ->
                result.fold(
                    onSuccess = { priceList ->
                        _prices.value = priceList
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                )
            }
        }
    }
    
    fun load24hrTickers() {
        viewModelScope.launch {
            repository.get24hrTicker().collect { result ->
                result.fold(
                    onSuccess = { tickerList ->
                        _tickers.value = tickerList
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to load tickers"
                        )
                    }
                )
            }
        }
    }
    
    fun loadKlines(symbol: String, interval: String = "1d", limit: Int = 30) {
        viewModelScope.launch {
            repository.getKlines(symbol, interval, limit).collect { result ->
                result.fold(
                    onSuccess = { klineList ->
                        _klines.value = klineList
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Failed to load chart data"
                        )
                    }
                )
            }
        }
    }
    
    // Legacy method for backward compatibility
    fun loadExchangeRates() {
        loadAllPrices()
    }
    
    fun convertCurrency(amount: Double, fromSymbol: String, toSymbol: String): Double {
        // Handle same currency conversion
        if (fromSymbol == toSymbol) return amount
        
        // Define fiat currencies
        val fiatCurrencies = setOf("USD", "EUR", "GBP", "PKR", "AED", "MYR", "SAR", "QAR", "KWD", "BHD", "OMR", "INR", "JPY", "CNY", "KRW", "AUD", "CAD", "CHF", "TRY", "RUB", "BRL", "ZAR", "NGN", "EGP", "PHP")
        
        val fromIsFiat = fromSymbol in fiatCurrencies
        val toIsFiat = toSymbol in fiatCurrencies
        
        return when {
            // Crypto to Crypto (existing logic)
            !fromIsFiat && !toIsFiat -> {
                val fromPrice = _prices.value.find { it.symbol == "${fromSymbol}USDT" }?.price?.toDoubleOrNull() ?: 1.0
                val toPrice = _prices.value.find { it.symbol == "${toSymbol}USDT" }?.price?.toDoubleOrNull() ?: 1.0
                
                if (fromSymbol == "USDT") {
                    amount / toPrice
                } else if (toSymbol == "USDT") {
                    amount * fromPrice
                } else {
                    (amount * fromPrice) / toPrice
                }
            }
            
            // Fiat to Fiat - Use approximate exchange rates
            fromIsFiat && toIsFiat -> {
                convertFiatToFiat(amount, fromSymbol, toSymbol)
            }
            
            // Crypto to Fiat
            !fromIsFiat && toIsFiat -> {
                val cryptoToUsdtPrice = _prices.value.find { it.symbol == "${fromSymbol}USDT" }?.price?.toDoubleOrNull() ?: 1.0
                val usdtAmount = if (fromSymbol == "USDT") amount else amount * cryptoToUsdtPrice
                
                // Convert USDT to fiat (assuming 1 USDT ≈ 1 USD for simplicity)
                convertFiatToFiat(usdtAmount, "USD", toSymbol)
            }
            
            // Fiat to Crypto
            fromIsFiat && !toIsFiat -> {
                // Convert fiat to USD first
                val usdAmount = convertFiatToFiat(amount, fromSymbol, "USD")
                
                // Then convert USD to crypto via USDT (assuming 1 USD ≈ 1 USDT)
                val toCryptoPrice = _prices.value.find { it.symbol == "${toSymbol}USDT" }?.price?.toDoubleOrNull() ?: 1.0
                
                if (toSymbol == "USDT") {
                    usdAmount
                } else {
                    usdAmount / toCryptoPrice
                }
            }
            
            else -> amount
        }
    }
    
    // Approximate fiat-to-fiat conversion using hardcoded exchange rates
    // In a real app, you would fetch these from a forex API
    private fun convertFiatToFiat(amount: Double, fromSymbol: String, toSymbol: String): Double {
        if (fromSymbol == toSymbol) return amount
        
        // Approximate exchange rates to USD (as of recent data)
        val usdRates = mapOf(
            "USD" to 1.0,
            "EUR" to 0.85,
            "GBP" to 0.73,
            "PKR" to 278.0,
            "AED" to 3.67,
            "MYR" to 4.47,
            "SAR" to 3.75,
            "QAR" to 3.64,
            "KWD" to 0.31,
            "BHD" to 0.38,
            "OMR" to 0.38,
            "INR" to 83.0,
            "JPY" to 150.0,
            "CNY" to 7.2,
            "KRW" to 1320.0,
            "AUD" to 1.52,
            "CAD" to 1.36,
            "CHF" to 0.88,
            "TRY" to 28.5,
            "RUB" to 92.0,
            "BRL" to 5.0,
            "ZAR" to 18.5,
            "NGN" to 460.0,
            "EGP" to 31.0,
            "PHP" to 56.0
        )
        
        val fromRate = usdRates[fromSymbol] ?: 1.0
        val toRate = usdRates[toSymbol] ?: 1.0
        
        // Convert: amount in fromSymbol -> USD -> toSymbol
        val usdAmount = amount / fromRate
        return usdAmount * toRate
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CurrencyUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)