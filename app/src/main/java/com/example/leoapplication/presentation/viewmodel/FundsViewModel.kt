package com.example.leoapplication.presentation.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.CryptoRepository
import com.example.leoapplication.data.repository.FundsRepository
import com.example.leoapplication.domain.model.Crypto
import com.example.leoapplication.domain.model.Funds
import com.example.leoapplication.domain.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FundsViewModel @Inject constructor(
    val repository: FundsRepository
) : ViewModel() {

    private val _funds = MutableLiveData<List<Funds>>()
    val funds: LiveData<List<Funds>> get() = _funds

    fun loadFunds() {
        viewModelScope.launch {
            val data = repository.getFunds()
            _funds.postValue(data)
        }
    }

    fun uploadSampleFunds() {
        viewModelScope.launch {
            val fundsList = listOf(
                Funds("1", "Vanguard Real Estate ETF", "VNQ", "https://example.com/vnq.png", 75.3),
                Funds("2", "Vanguard Global REIT ETF", "VNQI", "https://example.com/vnqi.png", 68.5),
                Funds("3", "iShares U.S. Real Estate ETF", "IYR", "https://example.com/iyr.png", 70.1),
                Funds("4", "Schwab U.S. REIT ETF", "SCHH", "https://example.com/schh.png", 64.8),
                Funds("5", "SPDR Dow Jones REIT ETF", "RWR", "https://example.com/rwr.png", 69.2),
                Funds("6", "iShares Global REIT ETF", "REET", "https://example.com/reet.png", 62.4),
                Funds("7", "Fidelity MSCI Real Estate ETF", "FREL", "https://example.com/frel.png", 66.7),
                Funds("8", "Real Estate Select Sector SPDR Fund", "XLRE", "https://example.com/xlre.png", 63.5),
                Funds("9", "First Trust North American Energy Infrastructure Fund", "FNI", "https://example.com/fni.png", 55.8),
                Funds("10", "First Trust Latin America AlphaDEX Fund", "FLN", "https://example.com/fln.png", 58.9),
                Funds("11", "Cohen & Steers REIT ETF", "ICF", "https://example.com/icf.png", 61.2),
                Funds("12", "Cohen & Steers Total Return Real Estate Fund", "RFI", "https://example.com/rfi.png", 59.6),
                Funds("13", "Invesco Active U.S. Real Estate ETF", "PSR", "https://example.com/psr.png", 60.1),
                Funds("14", "iShares Residential Real Estate ETF", "REZ", "https://example.com/rez.png", 57.4),
                Funds("15", "Technology Select Sector SPDR Fund", "XLK", "https://example.com/xlk.png", 72.3),
                Funds("16", "Financial Select Sector SPDR Fund", "XLF", "https://example.com/xlf.png", 68.9),
                Funds("17", "Energy Select Sector SPDR Fund", "XLE", "https://example.com/xle.png", 65.7),
                Funds("18", "Health Care Select Sector SPDR Fund", "XLV", "https://example.com/xlv.png", 71.4),
                Funds("19", "Vanguard Information Technology ETF", "VGT", "https://example.com/vgt.png", 74.1),
                Funds("20", "Vanguard Health Care ETF", "VHT", "https://example.com/vht.png", 70.8),
                Funds("21", "iShares Global Clean Energy ETF", "ICLN", "https://example.com/icln.png", 59.3),
                Funds("22", "Invesco Solar ETF", "TAN", "https://example.com/tan.png", 56.5),
                Funds("23", "Invesco WilderHill Clean Energy ETF", "PBW", "https://example.com/pbw.png", 54.9),
                Funds("24", "Fidelity Real Estate Index ETF", "FRELX", "https://example.com/frelx.png", 67.0),
                Funds("25", "Neo Fund", "NEO", "https://s2.coinmarketcap.com/static/img/coins/64x64/1376.png", 12.7)
            )


            repository.uploadFunds(fundsList)


        }

    }


}