package com.example.currencyconvertorapp.data.repository

import android.util.Log
import com.example.currencyconvertorapp.data.api.BinanceApiService
import com.example.currencyconvertorapp.data.model.CurrencyInfo
import com.example.currencyconvertorapp.data.model.ExchangeRate
import com.example.currencyconvertorapp.data.model.Ticker24hr
import com.example.currencyconvertorapp.data.model.TickerPrice
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val apiService: BinanceApiService
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    
    fun getExchangeRate(symbol: String): Flow<Result<TickerPrice>> = flow {
        try {
            val response = apiService.getExchangeRate(symbol)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get exchange rate")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getAllPrices(): Flow<Result<List<TickerPrice>>> = flow {
        try {
            val response = apiService.getAllPrices()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get all prices")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getAllTickers(): Flow<Result<List<Ticker24hr>>> = flow {
        try {
            val response = apiService.get24hrTicker()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                val errorMsg = "Failed to get tickers. HTTP ${response.code()}: ${response.message()}"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            val errorMsg = "Network error getting tickers: ${e.message}"
            emit(Result.failure(Exception(errorMsg)))
        }
    }
    
    fun get24hrTicker(symbol: String? = null): Flow<Result<List<Ticker24hr>>> = flow {
        try {
            val response = apiService.get24hrTicker(symbol)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get 24hr ticker")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getKlines(
        symbol: String,
        interval: String,
        limit: Int = 100
    ): Flow<Result<List<List<Any>>>> = flow {
        try {
            val response = apiService.getKlines(symbol, interval, limit)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get klines")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getKlineData(
        symbol: String,
        interval: String,
        limit: Int = 100
    ): Flow<Result<List<List<Any>>>> = flow {
        try {
            val response = apiService.getKlines(symbol, interval, limit)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get kline data")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    private var cachedCurrencies: List<CurrencyInfo>? = null
    private var lastCacheTime: Long = 0
    private val cacheValidityDuration = 5 * 60 * 1000L // 5 minutes
    
    fun getSupportedCurrencies(): Flow<Result<List<CurrencyInfo>>> = flow {
        val currentTime = System.currentTimeMillis()
        
        // First, emit cached data if available and valid
        if (cachedCurrencies != null && (currentTime - lastCacheTime) < cacheValidityDuration) {
            emit(Result.success(cachedCurrencies!!))
            return@flow
        }
        
        // Get hardcoded currencies (includes both crypto and fiat)
        val hardcodedList = getHardcodedCurrencies()
        
        // Immediately emit hardcoded currencies for fast UI response
        emit(Result.success(hardcodedList))
        
        // Then try to load from API in background and merge with hardcoded
        try {
            val response = apiService.getExchangeInfo()
            if (response.isSuccessful && response.body() != null) {
                val exchangeInfo = response.body()!!
                val symbols = exchangeInfo["symbols"] as? List<Map<String, Any>> ?: emptyList()
                
                // Extract unique base assets for ALL TRADING symbols (no popularity filter)
                val apiCurrencies = symbols
                    .mapNotNull { symbol ->
                        val baseAsset = symbol["baseAsset"] as? String
                        val status = symbol["status"] as? String
                        if (baseAsset != null && status == "TRADING") baseAsset else null
                    }
                    .distinct()
                    .map { currency ->
                        CurrencyInfo(
                            code = currency,
                            name = getCurrencyName(currency),
                            symbol = getCurrencySymbol(currency)
                        )
                    }
                
                // Merge hardcoded currencies with API currencies, prioritizing hardcoded
                val hardcodedCodes = hardcodedList.map { it.code }.toSet()
                val mergedCurrencies = (hardcodedList + apiCurrencies.filter { it.code !in hardcodedCodes })
                    .sortedBy { it.code }
                
                cachedCurrencies = mergedCurrencies
                lastCacheTime = currentTime
                emit(Result.success(mergedCurrencies))
            }
        } catch (e: Exception) {
            // If API fails, we already emitted hardcoded list, so just cache it
            Log.e("CurrencyRepository", "getSupportedCurrencies: API failed exception = ${e.message}" )
            cachedCurrencies = hardcodedList
            lastCacheTime = currentTime
        }
    }
    
    private fun getHardcodedCurrencies(): List<CurrencyInfo> {
        return listOf(
            // Cryptocurrencies
            CurrencyInfo("BTC", "Bitcoin", "₿"),
            CurrencyInfo("ETH", "Ethereum", "Ξ"),
            CurrencyInfo("BNB", "Binance Coin", "BNB"),
            CurrencyInfo("ADA", "Cardano", "₳"),
            CurrencyInfo("XRP", "Ripple", "XRP"),
            CurrencyInfo("DOT", "Polkadot", "DOT"),
            CurrencyInfo("LINK", "Chainlink", "LINK"),
            CurrencyInfo("LTC", "Litecoin", "Ł"),
            CurrencyInfo("BCH", "Bitcoin Cash", "BCH"),
            CurrencyInfo("UNI", "Uniswap", "UNI"),
            CurrencyInfo("USDT", "Tether", "$"),
            CurrencyInfo("USDC", "USD Coin", "$"),
            CurrencyInfo("BUSD", "Binance USD", "$"),
            
            // Fiat Currencies
            CurrencyInfo("USD", "US Dollar", "$"),
            CurrencyInfo("EUR", "Euro", "€"),
            CurrencyInfo("GBP", "British Pound", "£"),
            CurrencyInfo("PKR", "Pakistani Rupee", "₨"),
            CurrencyInfo("AED", "UAE Dirham", "د.إ"),
            CurrencyInfo("MYR", "Malaysian Ringgit", "RM"),
            CurrencyInfo("SAR", "Saudi Riyal", "﷼"),
            CurrencyInfo("QAR", "Qatari Riyal", "﷼"),
            CurrencyInfo("KWD", "Kuwaiti Dinar", "د.ك"),
            CurrencyInfo("BHD", "Bahraini Dinar", ".د.ب"),
            CurrencyInfo("OMR", "Omani Rial", "﷼"),
            CurrencyInfo("INR", "Indian Rupee", "₹"),
            CurrencyInfo("JPY", "Japanese Yen", "¥"),
            CurrencyInfo("CNY", "Chinese Yuan", "¥"),
            CurrencyInfo("KRW", "South Korean Won", "₩"),
            CurrencyInfo("AUD", "Australian Dollar", "A$"),
            CurrencyInfo("CAD", "Canadian Dollar", "C$"),
            CurrencyInfo("CHF", "Swiss Franc", "CHF"),
            CurrencyInfo("TRY", "Turkish Lira", "₺"),
            CurrencyInfo("RUB", "Russian Ruble", "₽"),
            CurrencyInfo("BRL", "Brazilian Real", "R$"),
            CurrencyInfo("ZAR", "South African Rand", "R"),
            CurrencyInfo("NGN", "Nigerian Naira", "₦"),
            CurrencyInfo("EGP", "Egyptian Pound", "£"),
            CurrencyInfo("PHP", "Philippine Peso", "₱")
        )
    }
    
    private fun getCurrencyName(code: String): String {
        return when (code) {
            // Cryptocurrencies
            "BTC" -> "Bitcoin"
            "ETH" -> "Ethereum"
            "BNB" -> "Binance Coin"
            "ADA" -> "Cardano"
            "XRP" -> "Ripple"
            "DOT" -> "Polkadot"
            "LINK" -> "Chainlink"
            "LTC" -> "Litecoin"
            "BCH" -> "Bitcoin Cash"
            "UNI" -> "Uniswap"
            "USDT" -> "Tether"
            "USDC" -> "USD Coin"
            "BUSD" -> "Binance USD"
            "DOGE" -> "Dogecoin"
            "MATIC" -> "Polygon"
            "SOL" -> "Solana"
            "AVAX" -> "Avalanche"
            "ATOM" -> "Cosmos"
            "FTM" -> "Fantom"
            "NEAR" -> "NEAR Protocol"
            
            // Fiat Currencies
            "USD" -> "US Dollar"
            "EUR" -> "Euro"
            "GBP" -> "British Pound"
            "PKR" -> "Pakistani Rupee"
            "AED" -> "UAE Dirham"
            "MYR" -> "Malaysian Ringgit"
            "SAR" -> "Saudi Riyal"
            "QAR" -> "Qatari Riyal"
            "KWD" -> "Kuwaiti Dinar"
            "BHD" -> "Bahraini Dinar"
            "OMR" -> "Omani Rial"
            "INR" -> "Indian Rupee"
            "JPY" -> "Japanese Yen"
            "CNY" -> "Chinese Yuan"
            "KRW" -> "South Korean Won"
            "AUD" -> "Australian Dollar"
            "CAD" -> "Canadian Dollar"
            "CHF" -> "Swiss Franc"
            "TRY" -> "Turkish Lira"
            "RUB" -> "Russian Ruble"
            "BRL" -> "Brazilian Real"
            "ZAR" -> "South African Rand"
            "NGN" -> "Nigerian Naira"
            "EGP" -> "Egyptian Pound"
            "PHP" -> "Philippine Peso"
            else -> code // Default to the code itself
        }
    }
    
    private fun getCurrencySymbol(code: String): String {
        return when (code) {
            // Cryptocurrencies
            "BTC" -> "₿"
            "ETH" -> "Ξ"
            "ADA" -> "₳"
            "LTC" -> "Ł"
            "USDT", "USDC", "BUSD" -> "$"
            "BNB" -> "🔶"
            "XRP" -> "◆"
            "DOT" -> "●"
            "LINK" -> "🔗"
            "BCH" -> "₿"
            "UNI" -> "🦄"
            "DOGE" -> "Ð"
            "MATIC" -> "◇"
            "SOL" -> "◎"
            "AVAX" -> "🔺"
            "ATOM" -> "⚛"
            "FTM" -> "👻"
            "NEAR" -> "Ⓝ"
            
            // Fiat Currencies
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "PKR" -> "₨"
            "AED" -> "د.إ"
            "MYR" -> "RM"
            "SAR" -> "﷼"
            "QAR" -> "﷼"
            "KWD" -> "د.ك"
            "BHD" -> ".د.ب"
            "OMR" -> "﷼"
            "INR" -> "₹"
            "JPY" -> "¥"
            "CNY" -> "¥"
            "KRW" -> "₩"
            "AUD" -> "A$"
            "CAD" -> "C$"
            "CHF" -> "CHF"
            "TRY" -> "₺"
            "RUB" -> "₽"
            "BRL" -> "R$"
            "ZAR" -> "R"
            "NGN" -> "₦"
            "EGP" -> "£"
            "PHP" -> "₱"
            else -> "" // Return empty string to avoid duplication
        }
    }
    
    // Legacy method for backward compatibility
    fun getAllExchangeRates(): Flow<Result<List<ExchangeRate>>> = flow {
        try {
            val response = apiService.getAllExchangeRates()
            if (response.isSuccessful && response.body() != null) {
                val exchangeRates = response.body()!!.map { tickerPrice ->
                    ExchangeRate(tickerPrice.symbol, tickerPrice.price)
                }
                emit(Result.success(exchangeRates))
            } else {
                emit(Result.failure(Exception("Failed to get exchange rates")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // WebSocket stream for cryptocurrency tickers
    fun getCryptoTickerStream(): Flow<Result<List<Ticker24hr>>> = callbackFlow {
        val cryptoSymbols = listOf(
            // Top 10 by Market Cap
            "BTCUSDT", "ETHUSDT", "BNBUSDT", "XRPUSDT", "ADAUSDT", "SOLUSDT", 
            "DOGEUSDT", "TRXUSDT", "TONUSDT", "AVAXUSDT",
            
            // DeFi & Layer 1
            "DOTUSDT", "MATICUSDT", "LINKUSDT", "UNIUSDT", "LTCUSDT", "BCHUSDT", 
            "NEARUSDT", "ATOMUSDT", "FILUSDT", "VETUSDT",
            
            // Layer 2 & Scaling
            "ARBUSDT", "OPUSDT", "LDOUSDT", "IMXUSDT",
            
            // Meme Coins
            "SHIBUSDT", "PEPEUSDT", "FLOKIUSDT", "BONKUSDT",
            
            // AI & Gaming
            "FETUSDT", "RENDERUSDT", "SANDUSDT", "MANAUSDT", "AXSUSDT",
            
            // Enterprise & Institutional
            "XLMUSDT", "ALGOUSDT", "HBARUSDT", "QNTUSDT", "INJUSDT",
            
            // Stablecoins & Wrapped
            "WBTCUSDT", "STETHUSDT",
            
            // Privacy & Security
            "XMRUSDT", "ZECUSDT",
            
            // Cross-chain & Interoperability
            "ICPUSDT", "FLOWUSDT", "APTUSDT", "SUIUSDT"
        )
        
        val client = OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/!ticker@arr")
            .build()

        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                println("Crypto WebSocket connection opened successfully")
                // Send connection success indicator
                trySend(Result.success(listOf(
                    Ticker24hr(
                        symbol = "CONNECTION_SUCCESS",
                        priceChange = "0",
                        priceChangePercent = "0",
                        weightedAvgPrice = "0",
                        prevClosePrice = "0",
                        lastPrice = "0",
                        lastQty = "0",
                        bidPrice = "0",
                        askPrice = "0",
                        openPrice = "0",
                        highPrice = "0",
                        lowPrice = "0",
                        volume = "0",
                        quoteVolume = "0",
                        openTime = 0L,
                        closeTime = 0L,
                        count = 0
                    )
                )))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    println("Received crypto WebSocket message: ${text.take(200)}...")
                    
                    val jsonElement = gson.fromJson(text, com.google.gson.JsonElement::class.java)
                    
                    if (jsonElement.isJsonArray) {
                        // Multi-stream format (array of tickers)
                        val tickerArray = jsonElement.asJsonArray
                        val cryptoTickers = mutableListOf<Ticker24hr>()
                        
                        for (tickerElement in tickerArray) {
                            val tickerObj = tickerElement.asJsonObject
                            val symbol = tickerObj.get("s")?.asString ?: ""
                            
                            // Only process crypto symbols we're interested in
                            if (cryptoSymbols.contains(symbol)) {
                                val ticker = Ticker24hr(
                                    symbol = symbol,
                                    priceChange = tickerObj.get("p")?.asString ?: "0",
                                    priceChangePercent = tickerObj.get("P")?.asString ?: "0",
                                    weightedAvgPrice = tickerObj.get("w")?.asString ?: "0",
                                    prevClosePrice = tickerObj.get("x")?.asString ?: "0",
                                    lastPrice = tickerObj.get("c")?.asString ?: "0",
                                    lastQty = tickerObj.get("Q")?.asString ?: "0",
                                    bidPrice = tickerObj.get("b")?.asString ?: "0",
                                    askPrice = tickerObj.get("a")?.asString ?: "0",
                                    openPrice = tickerObj.get("o")?.asString ?: "0",
                                    highPrice = tickerObj.get("h")?.asString ?: "0",
                                    lowPrice = tickerObj.get("l")?.asString ?: "0",
                                    volume = tickerObj.get("v")?.asString ?: "0",
                                    quoteVolume = tickerObj.get("q")?.asString ?: "0",
                                    openTime = tickerObj.get("O")?.asLong ?: 0L,
                                    closeTime = tickerObj.get("C")?.asLong ?: 0L,
                                    count = tickerObj.get("n")?.asInt ?: 0
                                )
                                cryptoTickers.add(ticker)
                                println("Processed crypto ticker: ${ticker.symbol} = ${ticker.lastPrice}")
                            }
                        }
                        
                        if (cryptoTickers.isNotEmpty()) {
                            trySend(Result.success(cryptoTickers))
                        }
                    }
                } catch (e: Exception) {
                    println("Crypto WebSocket JSON Parse Error: ${e.message}")
                    trySend(Result.failure(e))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                println("Crypto WebSocket connection failed: ${t.message}")
                trySend(Result.failure(t))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Crypto WebSocket closing: $code - $reason")
                webSocket.close(1000, null)
            }
        })

        awaitClose {
            println("Closing crypto WebSocket connection")
            webSocket.close(1000, "Flow cancelled")
        }
    }

    // WebSocket: observe @ticker stream for given pairs
    fun observeTickerStream(pairs: List<String>): kotlinx.coroutines.flow.Flow<Result<Ticker24hr>> = callbackFlow {
        // Use individual streams approach - more reliable than combined streams
        val streams = pairs.joinToString("/") { "${it.lowercase()}@ticker" }
        val url = "wss://stream.binance.com:9443/ws/$streams"
        val request = Request.Builder().url(url).build()

        // Event model (Binance @ticker fields)
        data class TickerStreamEvent(
            @SerializedName("e") val eventType: String? = null,
            @SerializedName("E") val eventTime: Long? = null,
            @SerializedName("s") val symbol: String = "",
            @SerializedName("p") val priceChange: String = "0",
            @SerializedName("P") val priceChangePercent: String = "0",
            @SerializedName("w") val weightedAvgPrice: String = "0",
            @SerializedName("x") val prevClosePrice: String = "0",
            @SerializedName("c") val lastPrice: String = "0",
            @SerializedName("Q") val lastQty: String = "0",
            @SerializedName("b") val bidPrice: String = "0",
            @SerializedName("B") val bidQty: String? = null,
            @SerializedName("a") val askPrice: String = "0",
            @SerializedName("A") val askQty: String? = null,
            @SerializedName("o") val openPrice: String = "0",
            @SerializedName("h") val highPrice: String = "0",
            @SerializedName("l") val lowPrice: String = "0",
            @SerializedName("v") val volume: String = "0",
            @SerializedName("q") val quoteVolume: String = "0",
            @SerializedName("O") val openTime: Long = 0,
            @SerializedName("C") val closeTime: Long = 0,
            @SerializedName("F") val firstId: Long? = null,
            @SerializedName("L") val lastId: Long? = null,
            @SerializedName("n") val count: Int = 0
        )

        fun toTicker24hr(ev: TickerStreamEvent): Ticker24hr {
            return Ticker24hr(
                symbol = ev.symbol,
                priceChange = ev.priceChange,
                priceChangePercent = ev.priceChangePercent,
                weightedAvgPrice = ev.weightedAvgPrice,
                prevClosePrice = ev.prevClosePrice,
                lastPrice = ev.lastPrice,
                lastQty = ev.lastQty,
                bidPrice = ev.bidPrice,
                askPrice = ev.askPrice,
                openPrice = ev.openPrice,
                highPrice = ev.highPrice,
                lowPrice = ev.lowPrice,
                volume = ev.volume,
                quoteVolume = ev.quoteVolume,
                openTime = ev.openTime,
                closeTime = ev.closeTime,
                count = ev.count
            )
        }

        val ws = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                println("WebSocket connected successfully to: $url")
                trySend(Result.success(Ticker24hr(
                    symbol = "CONNECTION_SUCCESS",
                    priceChange = "0",
                    priceChangePercent = "0",
                    weightedAvgPrice = "0",
                    prevClosePrice = "0",
                    lastPrice = "0",
                    lastQty = "0",
                    bidPrice = "0",
                    askPrice = "0",
                    openPrice = "0",
                    highPrice = "0",
                    lowPrice = "0",
                    volume = "0",
                    quoteVolume = "0",
                    openTime = 0,
                    closeTime = 0,
                    count = 0
                )))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("WebSocket received: $text")
                try {
                    // Parse JSON first
                    val json = gson.fromJson(text, JsonObject::class.java)
                    
                    if (json.has("stream") && json.has("data")) {
                        // Multi-stream format
                        val dataObj = json.getAsJsonObject("data")
                        val ev = gson.fromJson(dataObj, TickerStreamEvent::class.java)
                        if (ev != null) {
                            println("Parsed multi-stream event: ${ev.symbol}")
                            trySend(Result.success(toTicker24hr(ev)))
                        } else {
                            println("Failed to parse multi-stream TickerStreamEvent - got null")
                        }
                    } else if (json.has("e") && json.get("e").asString == "24hrTicker") {
                        // Single stream format - manual parsing since Gson is failing
                        println("Detected single-stream format, using manual parsing...")
                        
                        val ticker = Ticker24hr(
                            symbol = json.get("s")?.asString ?: "",
                            priceChange = json.get("p")?.asString ?: "0",
                            priceChangePercent = json.get("P")?.asString ?: "0",
                            weightedAvgPrice = json.get("w")?.asString ?: "0",
                            prevClosePrice = json.get("x")?.asString ?: "0",
                            lastPrice = json.get("c")?.asString ?: "0",
                            lastQty = json.get("Q")?.asString ?: "0",
                            bidPrice = json.get("b")?.asString ?: "0",
                            askPrice = json.get("a")?.asString ?: "0",
                            openPrice = json.get("o")?.asString ?: "0",
                            highPrice = json.get("h")?.asString ?: "0",
                            lowPrice = json.get("l")?.asString ?: "0",
                            volume = json.get("v")?.asString ?: "0",
                            quoteVolume = json.get("q")?.asString ?: "0",
                            openTime = json.get("O")?.asLong ?: 0,
                            closeTime = json.get("C")?.asLong ?: 0,
                            count = json.get("n")?.asInt ?: 0
                        )
                        
                        println("Manually parsed ticker: ${ticker.symbol} = ${ticker.lastPrice}")
                        trySend(Result.success(ticker))
                    } else {
                        println("Unknown WebSocket message format: $text")
                        println("JSON keys: ${json.keySet()}")
                    }
                } catch (ex: Exception) {
                    println("WebSocket JSON Parse Error: ${ex.message}")
                    println("Exception type: ${ex.javaClass.simpleName}")
                    ex.printStackTrace()
                    trySend(Result.failure(Exception("JSON Parse Error: ${ex.message}")))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                println("WebSocket failed: ${t.message}, Response: ${response?.code}")
                val errorMsg = when {
                    t.message?.contains("failed to connect") == true -> "Network connection failed - check internet"
                    t.message?.contains("timeout") == true -> "Connection timeout - try again"
                    response?.code == 403 -> "Access forbidden - API may be blocked"
                    response?.code == 429 -> "Rate limited - too many requests"
                    else -> "Connection failed: ${t.message ?: "Unknown error"}"
                }
                trySend(Result.failure(Exception(errorMsg)))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closing: $code - $reason")
                trySend(Result.failure(Exception("Connection closing: $reason")))
            }
        })

        awaitClose { 
            println("Closing WebSocket connection")
            ws.close(1000, "Closed by client") 
        }
    }
}