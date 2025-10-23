package com.example.currencyconvertorapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertorapp.data.model.CryptoInfo
import com.example.currencyconvertorapp.data.model.CryptoConstants
import com.example.currencyconvertorapp.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CryptoUiState(
    val cryptos: List<CryptoInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isConnected: Boolean = false
)

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CryptoUiState())
    val uiState: StateFlow<CryptoUiState> = _uiState.asStateFlow()

    init {
        loadCryptoData()
    }

    fun loadCryptoData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                repository.getCryptoTickerStream().collect { result ->
                    result.fold(
                        onSuccess = { tickers ->
                            val cryptoList = tickers
                                .filter { ticker -> 
                                    CryptoConstants.POPULAR_CRYPTOS.containsKey(ticker.symbol)
                                }
                                .map { ticker ->
                                    CryptoInfo(
                                        symbol = ticker.symbol,
                                        name = CryptoConstants.getCryptoName(ticker.symbol),
                                        currentPrice = ticker.lastPrice.toDoubleOrNull() ?: 0.0,
                                        priceChangePercent = ticker.priceChangePercent.toDoubleOrNull() ?: 0.0,
                                        volume24h = ticker.volume.toDoubleOrNull() ?: 0.0,
                                        high24h = ticker.highPrice.toDoubleOrNull(),
                                        low24h = ticker.lowPrice.toDoubleOrNull()
                                    )
                                }
                                .sortedByDescending { it.volume24h }
                            
                            _uiState.value = _uiState.value.copy(
                                cryptos = cryptoList,
                                isLoading = false,
                                isConnected = true,
                                errorMessage = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isConnected = false,
                                errorMessage = error.message ?: "Failed to load crypto data"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isConnected = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun refreshData() {
        loadCryptoData()
    }
}