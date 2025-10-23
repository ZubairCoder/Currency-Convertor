package com.example.currencyconvertorapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.currencyconvertorapp.presentation.screen.*

object Screen {
    const val HOME = "home"
    const val REAL_TIME = "real_time_currency"
    const val MULTI_CURRENCY = "multi_currency"
    const val TREND_CHARTS = "trend_charts"
    const val CRYPTO = "crypto"
}

@Composable
fun CurrencyConverterNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "real_time_currency"
    ) {
        composable("real_time_currency") {
            RealTimeCurrencyScreen()
        }
        composable("multi_currency") {
            MultiCurrencyScreen()
        }
        composable("multi_8_currency") {
            Multi8CurrencyScreen()
        }
        composable("trend_charts") {
            TrendChartsScreen()
        }
        composable("exchange_rate_list") {
            ExchangeRateListScreen()
        }
        composable("currency_simulation") {
            CurrencySimulationScreen()
        }
        composable("exchange_rate_adjustment") {
            ExchangeRateAdjustmentScreen()
        }
        composable("currency_profile") {
            CurrencyProfileScreen()
        }
        composable(Screen.CRYPTO) {
            CryptoScreen()
        }
    }
}