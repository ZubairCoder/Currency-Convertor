package com.example.currencyconvertorapp.data.model

import com.google.gson.annotations.SerializedName

data class ExchangeRate(
    val symbol: String,
    val price: String
)

data class TickerPrice(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("price")
    val price: String
)

data class Ticker24hr(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("priceChange")
    val priceChange: String,
    @SerializedName("priceChangePercent")
    val priceChangePercent: String,
    @SerializedName("weightedAvgPrice")
    val weightedAvgPrice: String,
    @SerializedName("prevClosePrice")
    val prevClosePrice: String,
    @SerializedName("lastPrice")
    val lastPrice: String,
    @SerializedName("lastQty")
    val lastQty: String,
    @SerializedName("bidPrice")
    val bidPrice: String,
    @SerializedName("askPrice")
    val askPrice: String,
    @SerializedName("openPrice")
    val openPrice: String,
    @SerializedName("highPrice")
    val highPrice: String,
    @SerializedName("lowPrice")
    val lowPrice: String,
    @SerializedName("volume")
    val volume: String,
    @SerializedName("quoteVolume")
    val quoteVolume: String,
    @SerializedName("openTime")
    val openTime: Long,
    @SerializedName("closeTime")
    val closeTime: Long,
    @SerializedName("count")
    val count: Int
)

// Add the missing model classes
typealias ExchangeRateResponse = TickerPrice
typealias TickerResponse = Ticker24hr

data class KlineData(
    @SerializedName("0")
    val openTime: Long,
    @SerializedName("1")
    val open: String,
    @SerializedName("2")
    val high: String,
    @SerializedName("3")
    val low: String,
    @SerializedName("4")
    val close: String,
    @SerializedName("5")
    val volume: String,
    @SerializedName("6")
    val closeTime: Long,
    @SerializedName("7")
    val quoteAssetVolume: String,
    @SerializedName("8")
    val numberOfTrades: Int,
    @SerializedName("9")
    val takerBuyBaseAssetVolume: String,
    @SerializedName("10")
    val takerBuyQuoteAssetVolume: String,
    @SerializedName("11")
    val ignore: String
)

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String = ""
)