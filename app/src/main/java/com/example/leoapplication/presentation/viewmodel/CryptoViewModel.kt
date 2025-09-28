package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.CryptoRepository
import com.example.leoapplication.domain.model.Crypto
import com.example.leoapplication.domain.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    val repository: CryptoRepository
) : ViewModel() {

    private val _cryptos = MutableLiveData<List<Crypto>>()
    val cryptos: LiveData<List<Crypto>> get() = _cryptos


    fun loadCryptos() {
        viewModelScope.launch {
            val data = repository.getCrypto()
            _cryptos.postValue(data)
        }
    }

    fun uploadSampleCryptos() {
        viewModelScope.launch {
            val cryptos = listOf(
                Crypto(
                    "1",
                    "Bitcoin",
                    "BTC",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
                    29345.2
                ),
                Crypto(
                    "2",
                    "Ethereum",
                    "ETH",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1027.png",
                    1843.6
                ),
                Crypto(
                    "3",
                    "Binance Coin",
                    "BNB",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1839.png",
                    319.8
                ),
                Crypto(
                    "4",
                    "Cardano",
                    "ADA",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/2010.png",
                    0.44
                ),
                Crypto(
                    "5",
                    "Solana",
                    "SOL",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/5426.png",
                    21.5
                ),
                Crypto(
                    "6",
                    "Ripple",
                    "XRP",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/52.png",
                    0.63
                ),
                Crypto(
                    "7",
                    "Polkadot",
                    "DOT",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/6636.png",
                    6.2
                ),
                Crypto(
                    "8",
                    "Dogecoin",
                    "DOGE",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/74.png",
                    0.078
                ),
                Crypto(
                    "9",
                    "Shiba Inu",
                    "SHIB",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/5994.png",
                    0.0000092
                ),
                Crypto(
                    "10",
                    "Litecoin",
                    "LTC",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/2.png",
                    64.3
                ),
                Crypto(
                    "11",
                    "Chainlink",
                    "LINK",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1975.png",
                    7.1
                ),
                Crypto(
                    "12",
                    "Uniswap",
                    "UNI",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/7083.png",
                    6.4
                ),
                Crypto(
                    "13",
                    "Avalanche",
                    "AVAX",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/5805.png",
                    17.8
                ),
                Crypto(
                    "14",
                    "Terra",
                    "LUNA",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/4172.png",
                    0.0005
                ),
                Crypto(
                    "15",
                    "Cosmos",
                    "ATOM",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/3794.png",
                    10.3
                ),
                Crypto(
                    "16",
                    "Algorand",
                    "ALGO",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/4030.png",
                    0.38
                ),
                Crypto(
                    "17",
                    "VeChain",
                    "VET",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/3077.png",
                    0.022
                ),
                Crypto(
                    "18",
                    "Stellar",
                    "XLM",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/512.png",
                    0.11
                ),
                Crypto(
                    "19",
                    "TRON",
                    "TRX",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1958.png",
                    0.062
                ),
                Crypto(
                    "20",
                    "Monero",
                    "XMR",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/328.png",
                    168.9
                ),
                Crypto(
                    "21",
                    "Tezos",
                    "XTZ",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/2011.png",
                    1.15
                ),
                Crypto(
                    "22",
                    "Filecoin",
                    "FIL",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/2280.png",
                    5.3
                ),
                Crypto(
                    "23",
                    "Hedera",
                    "HBAR",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/4642.png",
                    0.07
                ),
                Crypto(
                    "24",
                    "EOS",
                    "EOS",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1765.png",
                    1.05
                ),
                Crypto(
                    "25",
                    "Neo",
                    "NEO",
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/1376.png",
                    12.7
                )
            )

            repository.uploadCryptos(cryptos)


        }

    }


}