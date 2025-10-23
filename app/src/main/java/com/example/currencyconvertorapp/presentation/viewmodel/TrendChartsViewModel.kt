package com.example.currencyconvertorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertorapp.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendChartsViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrendChartsUiState())
    val uiState: StateFlow<TrendChartsUiState> = _uiState.asStateFlow()
    
    fun loadChartData(symbol: String, interval: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            repository.getKlineData(symbol, interval).collect { result ->
                result.fold(
                    onSuccess = { klineData ->
                        val chartData = klineData.map { kline ->
                            ChartDataPoint(
                                timestamp = (kline[0] as? Number)?.toLong() ?: 0L,
                                open = (kline[1] as? String)?.toDoubleOrNull() ?: 0.0,
                                high = (kline[2] as? String)?.toDoubleOrNull() ?: 0.0,
                                low = (kline[3] as? String)?.toDoubleOrNull() ?: 0.0,
                                close = (kline[4] as? String)?.toDoubleOrNull() ?: 0.0,
                                volume = (kline[5] as? String)?.toDoubleOrNull() ?: 0.0
                            )
                        }
                        _uiState.value = _uiState.value.copy(
                            chartData = chartData,
                            isLoading = false,
                            selectedSymbol = symbol,
                            selectedInterval = interval,
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
        }
    }
    
    fun updateSymbol(symbol: String) {
        loadChartData(symbol, _uiState.value.selectedInterval)
    }
    
    fun updateInterval(interval: String) {
        loadChartData(_uiState.value.selectedSymbol, interval)
    }
}

data class TrendChartsUiState(
    val chartData: List<ChartDataPoint> = emptyList(),
    val selectedSymbol: String = "BTCUSDT",
    val selectedInterval: String = "1h",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChartDataPoint(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)