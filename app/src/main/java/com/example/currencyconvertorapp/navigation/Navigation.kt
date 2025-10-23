package com.example.currencyconvertorapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.currencyconvertorapp.presentation.screen.CryptoScreen
import com.example.currencyconvertorapp.presentation.screens.HomeScreen
import com.example.currencyconvertorapp.presentation.screen.RealTimeCurrencyScreen
import com.example.currencyconvertorapp.presentation.screens.MultiCurrencyConverterScreen
import com.example.currencyconvertorapp.presentation.screens.Multi8CurrencyConverterScreen
import com.example.currencyconvertorapp.presentation.screens.TrendChartsScreen
import com.example.currencyconvertorapp.presentation.screen.ExchangeRateListScreen
import com.example.currencyconvertorapp.presentation.screen.CurrencySimulationScreen
import com.example.currencyconvertorapp.presentation.screen.ExchangeRateAdjustmentScreen
import com.example.currencyconvertorapp.presentation.screen.CurrencyProfileScreen
import com.example.currencyconvertorapp.presentation.screens.FiatCurrencyConverterScreen

// Define navigation routes
object Screen {
    const val HOME = "home"
    const val REAL_TIME = "real_time"
    const val MULTI_CURRENCY = "multi_currency"
    const val MULTI_8_CURRENCY = "multi_8_currency"
    const val FIAT_CURRENCY = "fiat_currency"
    const val TREND_CHARTS = "trend_charts"
    const val EXCHANGE_RATE_LIST = "exchange_rate_list"
    const val CURRENCY_SIMULATION = "currency_simulation"
    const val EXCHANGE_RATE_ADJUSTMENT = "exchange_rate_adjustment"
    const val CURRENCY_PROFILE = "currency_profile"
    const val CRYPTO = "CRYPTO"
}

@Composable
fun CurrencyConverterNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HOME,
        modifier = modifier
    ) {
        composable(Screen.HOME) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.REAL_TIME) {
            RealTimeCurrencyScreen()
        }
        
        composable(Screen.MULTI_CURRENCY) {
            MultiCurrencyConverterScreen(navController = navController)
        }
        
        composable(Screen.MULTI_8_CURRENCY) {
            Multi8CurrencyConverterScreen(navController = navController)
        }
        
        composable(Screen.FIAT_CURRENCY) {
            FiatCurrencyConverterScreen(navController = navController)
        }
        
        composable(Screen.TREND_CHARTS) {
            TrendChartsScreen(navController = navController)
        }
        
        composable(Screen.EXCHANGE_RATE_LIST) {
            ExchangeRateListScreen()
        }
        
        composable(Screen.CURRENCY_SIMULATION) {
            CurrencySimulationScreen()
        }
        
        composable(Screen.EXCHANGE_RATE_ADJUSTMENT) {
            ExchangeRateAdjustmentScreen()
        }
        
        composable(Screen.CURRENCY_PROFILE) {
            CurrencyProfileScreen()
        }
        composable(Screen.CRYPTO) {
            CryptoScreen()
        }
    }
}