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
class MultiCurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MultiCurrencyUiState())
    val uiState: StateFlow<MultiCurrencyUiState> = _uiState.asStateFlow()
    
    fun convertCurrency(amount: Double, fromCurrency: String, toCurrencies: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val conversions = mutableMapOf<String, Double>()
            
            toCurrencies.forEach { toCurrency ->
                val symbol = "${fromCurrency}${toCurrency}"
                repository.getExchangeRate(symbol).collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            val rate = response.price.toDoubleOrNull() ?: 0.0
                            conversions[toCurrency] = amount * rate
                        },
                        onFailure = { 
                            conversions[toCurrency] = 0.0
                        }
                    )
                }
            }
            
            _uiState.value = _uiState.value.copy(
                conversions = conversions,
                isLoading = false,
                amount = amount,
                fromCurrency = fromCurrency
            )
        }
    }
    
    fun updateAmount(amount: String) {
        val doubleAmount = amount.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(amount = doubleAmount)
    }
    
    fun updateFromCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(fromCurrency = currency)
    }
}

data class MultiCurrencyUiState(
    val amount: Double = 1.0,
    val fromCurrency: String = "BTC",
    val conversions: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)