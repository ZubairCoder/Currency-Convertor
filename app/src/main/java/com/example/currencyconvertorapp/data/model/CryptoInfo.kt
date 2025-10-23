package com.example.currencyconvertorapp.data.model

data class CryptoInfo(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val priceChangePercent: Double,
    val volume24h: Double,
    val marketCap: Double? = null,
    val high24h: Double? = null,
    val low24h: Double? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

// Popular cryptocurrencies with their full names
object CryptoConstants {
    val POPULAR_CRYPTOS = mapOf(
        // Top 10 by Market Cap
        "BTCUSDT" to "Bitcoin",
        "ETHUSDT" to "Ethereum", 
        "BNBUSDT" to "BNB",
        "XRPUSDT" to "XRP",
        "ADAUSDT" to "Cardano",
        "SOLUSDT" to "Solana",
        "DOGEUSDT" to "Dogecoin",
        "TRXUSDT" to "TRON",
        "TONUSDT" to "Toncoin",
        "AVAXUSDT" to "Avalanche",
        
        // DeFi & Layer 1
        "DOTUSDT" to "Polkadot",
        "MATICUSDT" to "Polygon",
        "LINKUSDT" to "Chainlink",
        "UNIUSDT" to "Uniswap",
        "LTCUSDT" to "Litecoin",
        "BCHUSDT" to "Bitcoin Cash",
        "NEARUSDT" to "NEAR Protocol",
        "ATOMUSDT" to "Cosmos",
        "FILUSDT" to "Filecoin",
        "VETUSDT" to "VeChain",
        
        // Layer 2 & Scaling
        "ARBUSDT" to "Arbitrum",
        "OPUSDT" to "Optimism",
        "LDOUSDT" to "Lido DAO",
        "IMXUSDT" to "Immutable X",
        
        // Meme Coins
        "SHIBUSDT" to "Shiba Inu",
        "PEPEUSDT" to "Pepe",
        "FLOKIUSDT" to "Floki",
        "BONKUSDT" to "Bonk",
        
        // AI & Gaming
        "FETUSDT" to "Fetch.ai",
        "RENDERUSDT" to "Render Token",
        "SANDUSDT" to "The Sandbox",
        "MANAUSDT" to "Decentraland",
        "AXSUSDT" to "Axie Infinity",
        
        // Enterprise & Institutional
        "XLMUSDT" to "Stellar",
        "ALGOUSDT" to "Algorand",
        "HBARUSDT" to "Hedera",
        "QNTUSDT" to "Quant",
        "INJUSDT" to "Injective",
        
        // Stablecoins & Wrapped
        "WBTCUSDT" to "Wrapped Bitcoin",
        "STETHUSDT" to "Lido Staked Ether",
        
        // Privacy & Security
        "XMRUSDT" to "Monero",
        "ZECUSDT" to "Zcash",
        
        // Cross-chain & Interoperability
        "ICPUSDT" to "Internet Computer",
        "FLOWUSDT" to "Flow",
        "APTUSDT" to "Aptos",
        "SUIUSDT" to "Sui"
    )
    
    fun getCryptoName(symbol: String): String {
        return POPULAR_CRYPTOS[symbol] ?: symbol.replace("USDT", "")
    }
    
    fun getCryptoSymbol(symbol: String): String {
        return symbol.replace("USDT", "")
    }
}