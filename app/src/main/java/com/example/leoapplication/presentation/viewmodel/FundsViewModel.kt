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
                Funds("1", "Vanguard Real Estate ETF", "VNQ", "https://logo.clearbit.com/vanguard.com", 75.3),
                Funds("2", "Vanguard Global REIT ETF", "VNQI", "https://logo.clearbit.com/vanguard.com", 68.5),
                Funds("3", "iShares U.S. Real Estate ETF", "IYR", "https://logo.clearbit.com/ishares.com", 70.1),
                Funds("4", "Schwab U.S. REIT ETF", "SCHH", "https://logo.clearbit.com/schwab.com", 64.8),
                Funds("5", "SPDR Dow Jones REIT ETF", "RWR", "https://logo.clearbit.com/ssga.com", 69.2),
                Funds("6", "iShares Global REIT ETF", "REET", "https://logo.clearbit.com/ishares.com", 62.4),
                Funds("7", "Fidelity MSCI Real Estate ETF", "FREL", "https://logo.clearbit.com/fidelity.com", 66.7),
                Funds("8", "Real Estate Select Sector SPDR Fund", "XLRE", "https://logo.clearbit.com/ssga.com", 63.5),
                Funds("9", "First Trust North American Energy Infrastructure Fund", "FNI", "https://logo.clearbit.com/firsttrust.com", 55.8),
                Funds("10", "First Trust Latin America AlphaDEX Fund", "FLN", "https://logo.clearbit.com/firsttrust.com", 58.9),
                Funds("11", "Cohen & Steers REIT ETF", "ICF", "https://logo.clearbit.com/cohenandsteers.com", 61.2),
                Funds("12", "Cohen & Steers Total Return Real Estate Fund", "RFI", "https://logo.clearbit.com/cohenandsteers.com", 59.6),
                Funds("13", "Invesco Active U.S. Real Estate ETF", "PSR", "https://logo.clearbit.com/invesco.com", 60.1),
                Funds("14", "iShares Residential Real Estate ETF", "REZ", "https://logo.clearbit.com/ishares.com", 57.4),
                Funds("15", "Technology Select Sector SPDR Fund", "XLK", "https://logo.clearbit.com/ssga.com", 72.3),
                Funds("16", "Financial Select Sector SPDR Fund", "XLF", "https://logo.clearbit.com/ssga.com", 68.9),
                Funds("17", "Energy Select Sector SPDR Fund", "XLE", "https://logo.clearbit.com/ssga.com", 65.7),
                Funds("18", "Health Care Select Sector SPDR Fund", "XLV", "https://logo.clearbit.com/ssga.com", 71.4),
                Funds("19", "Vanguard Information Technology ETF", "VGT", "https://logo.clearbit.com/vanguard.com", 74.1),
                Funds("20", "Vanguard Health Care ETF", "VHT", "https://logo.clearbit.com/vanguard.com", 70.8),
                Funds("21", "iShares Global Clean Energy ETF", "ICLN", "https://logo.clearbit.com/ishares.com", 59.3),
                Funds("22", "Invesco Solar ETF", "TAN", "https://logo.clearbit.com/invesco.com", 56.5),
                Funds("23", "Invesco WilderHill Clean Energy ETF", "PBW", "https://logo.clearbit.com/invesco.com", 54.9),
                Funds("24", "Fidelity Real Estate Index ETF", "FRELX", "https://logo.clearbit.com/fidelity.com", 67.0),
                Funds("25", "Neo Fund", "NEO", "https://s2.coinmarketcap.com/static/img/coins/64x64/1376.png", 12.7)
            )


            repository.uploadFunds(fundsList)


        }

    }


}