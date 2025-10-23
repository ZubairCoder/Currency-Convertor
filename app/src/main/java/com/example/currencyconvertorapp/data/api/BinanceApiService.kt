package com.example.currencyconvertorapp.data.api

import com.example.currencyconvertorapp.data.model.ExchangeRate
import com.example.currencyconvertorapp.data.model.Ticker24hr
import com.example.currencyconvertorapp.data.model.TickerPrice
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApiService {
    
    @GET("api/v3/ticker/price")
    suspend fun getAllPrices(): Response<List<TickerPrice>>
    
    @GET("api/v3/ticker/price")
    suspend fun getPrice(@Query("symbol") symbol: String): Response<TickerPrice>
    
    @GET("api/v3/ticker/24hr")
    suspend fun get24hrTicker(@Query("symbol") symbol: String? = null): Response<List<Ticker24hr>>
    
    @GET("api/v3/klines")
    suspend fun getKlines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("limit") limit: Int = 30
    ): Response<List<List<Any>>>
    
    @GET("api/v3/exchangeInfo")
    suspend fun getExchangeInfo(): Response<Map<String, Any>>

    // Consistent method signatures
    @GET("api/v3/ticker/price")
    suspend fun getExchangeRate(@Query("symbol") symbol: String): Response<TickerPrice>

    @GET("api/v3/ticker/price")
    suspend fun getAllExchangeRates(): Response<List<TickerPrice>>
    
    // Alias methods for backward compatibility
    suspend fun getAllTickers(): Response<List<Ticker24hr>> = get24hrTicker()
    
    suspend fun getKlineData(
        symbol: String,
        interval: String,
        limit: Int = 100
    ): Response<List<List<Any>>> = getKlines(symbol, interval, limit)
}