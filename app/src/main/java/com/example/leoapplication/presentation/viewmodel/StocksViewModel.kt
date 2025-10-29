package com.example.leoapplication.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leoapplication.data.repository.ShareRepository
import com.example.leoapplication.data.model.Share
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class StocksViewModel  @Inject constructor(
    private val repository: ShareRepository
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("shares")


    private val _shares = MutableLiveData<List<Share>>()
    val shares: LiveData<List<Share>> get() = _shares

    fun loadShares() {
        viewModelScope.launch {
            val data = repository.getShares()
            _shares.postValue(data)
        }
    }

    fun uploadSampleShares() {
        viewModelScope.launch {
            val shares = listOf(
                Share("1", "Apple Inc.", "https://logo.clearbit.com/apple.com", 175.32),
                Share("2", "Tesla, Inc.", "https://logo.clearbit.com/tesla.com", 231.45),
                Share("3", "Microsoft", "https://logo.clearbit.com/microsoft.com", 320.1),
                Share("4", "Amazon", "https://logo.clearbit.com/amazon.com", 129.5),
                Share("5", "Google", "https://logo.clearbit.com/google.com", 138.9),
                Share("6", "Meta Platforms", "https://logo.clearbit.com/meta.com", 305.4),
                Share("7", "Netflix", "https://logo.clearbit.com/netflix.com", 385.75),
                Share("8", "NVIDIA", "https://logo.clearbit.com/nvidia.com", 465.3),
                Share("9", "Intel", "https://logo.clearbit.com/intel.com", 36.4),
                Share("10", "Coca-Cola", "https://logo.clearbit.com/coca-cola.com", 59.2),
                Share("11", "Pepsi", "https://logo.clearbit.com/pepsi.com", 54.7),
                Share("12", "Samsung", "https://logo.clearbit.com/samsung.com", 75.3),
                Share("13", "Sony", "https://logo.clearbit.com/sony.com", 102.1),
                Share("14", "Toyota", "https://logo.clearbit.com/toyota.com", 150.6),
                Share("15", "Honda", "https://logo.clearbit.com/honda.com", 98.4)
            )

            shares.forEach { share ->
                collection.document(share.id).set(share)
            }
        }
    }
}
