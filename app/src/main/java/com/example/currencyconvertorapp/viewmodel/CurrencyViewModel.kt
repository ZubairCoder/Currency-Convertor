//package com.example.currencyconvertorapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.currencyconvertorapp.data.repository.CurrencyRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class CurrencyViewModel @Inject constructor(
//    private val repository: CurrencyRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(CurrencyUiState())
//    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
//
////    fun loadExchangeRates() {
////        viewModelScope.launch {
////            _uiState.value = _uiState.value.copy(isLoading = true)
////
////            repository.getAllExchangeRates().collect { result ->
////                result.fold(
////                    onSuccess = { rates ->
////                        val rateMap = rates.associate { it.symbol to it.price.toDoubleOrNull() ?: 0.0 }
////                        _uiState.value = _uiState.value.copy(
////                            exchangeRates = rateMap,
////                            isLoading = false,
////                            error = null
////                        )
////                    },
////                    onFailure = { error ->
////                        _uiState.value = _uiState.value.copy(
////                            error = error.message,
////                            isLoading = false
////                        )
////                    }
////                )
////            }
////        }
////    }
//}
//
//data class CurrencyUiState(
//    val exchangeRates: Map<String, Double> = emptyMap(),
//    val isLoading: Boolean = false,
//    val error: String? = null
//)