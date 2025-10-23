package com.example.currencyconvertorapp

import android.app.Application
import com.example.currencyconvertorapp.data.api.BinanceApiService
import com.example.currencyconvertorapp.data.repository.CurrencyRepository
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidApp
class CurrencyConverterApplication : Application() {
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.binance.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val binanceApiService by lazy {
        retrofit.create(BinanceApiService::class.java)
    }
    
    val currencyRepository by lazy {
        CurrencyRepository(binanceApiService)
    }
}