package com.example.currencyconvertorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertorapp.data.model.TickerResponse
import com.example.currencyconvertorapp.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealTimeCurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RealTimeCurrencyUiState())
    val uiState: StateFlow<RealTimeCurrencyUiState> = _uiState.asStateFlow()
    
    private val _selectedPairs = MutableStateFlow(
        listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "ADAUSDT", "XRPUSDT")
    )
    val selectedPairs: StateFlow<List<String>> = _selectedPairs.asStateFlow()
    
    private var autoRefreshJob: Job? = null
    private var wsJob: Job? = null

    init {
        loadRealTimeData()
        // Removed auto-refresh polling to keep UI fully real-time
        // startAutoRefresh(15_000)
    }
    
    fun loadRealTimeData() {
        wsJob?.cancel()
        autoRefreshJob?.cancel() // Cancel any existing REST fallback
        wsJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val latest = mutableMapOf<String, TickerResponse>()

            try {
                repository.observeTickerStream(_selectedPairs.value).collect { result ->
                    result.fold(
                        onSuccess = { ticker ->
                            // Skip connection success indicator
                            if (ticker.symbol == "CONNECTION_SUCCESS") {
                                println("WebSocket connection established successfully")
                                return@fold
                            }
                            
                            println("Received ticker update: ${ticker.symbol} = ${ticker.lastPrice}")
                            
                            latest[ticker.symbol] = ticker
                            _uiState.value = _uiState.value.copy(
                                tickers = _selectedPairs.value.mapNotNull { latest[it] },
                                isLoading = false,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            println("WebSocket error: ${error.message}")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "WebSocket error: ${error.message}"
                            )
                            // Don't call fallback immediately, let user see the error
                        }
                    )
                }
            } catch (e: Exception) {
                println("WebSocket collection error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Connection error: ${e.message}"
                )
            }
        }
    }

    private fun loadWithRestFallback() {
        wsJob?.cancel() // Cancel WebSocket job
        autoRefreshJob = viewModelScope.launch {
            // Show immediate message about the ban
            _uiState.value = _uiState.value.copy(
                isLoading = false, 
                error = "Binance API rate limit exceeded. Switching to WebSocket-only mode. Retrying in 60 seconds..."
            )
            
            // Wait 60 seconds then retry WebSocket (much more reasonable than waiting for the actual ban)
            delay(60_000) // 60 seconds
            
            _uiState.value = _uiState.value.copy(
                isLoading = true, 
                error = "Retrying WebSocket connection..."
            )
            
            // Retry WebSocket connection
            loadRealTimeData()
        }
    }

    fun refreshData() {
        loadRealTimeData()
    }

    private fun startAutoRefresh(intervalMs: Long = 15_000) {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getAllTickers().collect { result ->
                    result.fold(
                        onSuccess = { tickers ->
                            val filteredTickers = tickers.filter { ticker ->
                                _selectedPairs.value.contains(ticker.symbol)
                            }
                            _uiState.value = _uiState.value.copy(
                                tickers = filteredTickers,
                                isLoading = false,
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    )
                }
                delay(intervalMs)
            }
        }
    }
    
    override fun onCleared() {
        wsJob?.cancel()
        autoRefreshJob?.cancel()
        super.onCleared()
    }
}

data class RealTimeCurrencyUiState(
    val tickers: List<TickerResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)