package com.example.leoapplication.presentation.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.leoapplication.domain.model.Card
//import com.example.leoapplication.domain.model.User
//import com.example.leoapplication.domain.usecase.AddCardToUserUseCase
//import com.example.leoapplication.domain.usecase.CreateCardUseCase
//import com.example.leoapplication.domain.usecase.CreateUserUseCase
//import com.google.firebase.auth.FirebaseAuth
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class NewUserInfoVM @Inject constructor(
//    private val createUserUseCase: CreateUserUseCase,
//    private val createCardUseCase: CreateCardUseCase,
//    private val addCardToUserUseCase: AddCardToUserUseCase
//) : ViewModel() {
//
//    val registrationSuccess = MutableLiveData<Boolean>()
//    val error = MutableLiveData<String?>()
//    val bankCard = MutableLiveData<Card?>() // ← əlavə edildi
//
//    fun registerUser(fullName: String, email: String, phone: String, password: String) {
//        val auth = FirebaseAuth.getInstance()
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val firebaseUser = auth.currentUser
//                    val uid = firebaseUser?.uid ?: return@addOnCompleteListener
//
//                    viewModelScope.launch {
//                        try {
//                            val cleanPhone = phone.replace("+994", "").replace(" ", "")
//                            val newUser = User(
//                                uid = uid,               // ← UID buraya əlavə olunur
//                                fullName = fullName,
//                                email = email,
//                                phone = cleanPhone
//                            )
//                            createUserUseCase(newUser)
//
//                            val newCard = Card(
//                                cardNumber = (1..16).map { (0..9).random() }.joinToString(""),
//                                cvv = (100..999).random().toString(),
//                                mm = (1..12).random().toString().padStart(2, '0'),
//                                yy = ((23..30).random()).toString(),
//                                balance = 0.0,
//                                ownerPhone = cleanPhone,
//                                isNewCard = true
//                            )
//
//                            createCardUseCase(newCard)
//                            addCardToUserUseCase(cleanPhone, newCard.cardNumber)
//
//                            bankCard.value = newCard
//                            registrationSuccess.value = true
//                        } catch (e: Exception) {
//                            error.value = e.message
//                        }
//                    }
//                } else {
//                    error.value = task.exception?.message
//                }
//            }
//    }
//}
//
