package com.example.leoapplication.data.repository


import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.data.remote.FirebaseAuthDataSource
import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.domain.repository.AuthRepository
import com.example.leoapplication.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : AuthRepository {

    override fun sendVerificationCode(
        phoneNumber: String,
        activity: androidx.fragment.app.FragmentActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        authDataSource.sendVerificationCode(phoneNumber, activity, callbacks)
    }

    override suspend fun verifyPhoneNumber(credential: PhoneAuthCredential): Result<String> {
        return authDataSource.verifyPhoneNumber(credential)
    }

    override suspend fun createUserProfile(user: User): Result<Unit> {
        return firestoreDataSource.createUser(user)
    }

    override suspend fun createInitialCard(userId: String, phoneNumber: String): Result<Card> {
        return try {

            val userResult = firestoreDataSource.getUser(userId)
            val fullName = userResult.getOrNull()?.fullName ?: "CARD HOLDER"

            // Kart ID yarat
            val cardId = FirebaseFirestore.getInstance()
                .collection(Constants.CARDS_COLLECTION)
                .document()
                .id


            val generatedCard = firestoreDataSource.generateFullCard(phoneNumber, fullName)


            val card = generatedCard.copy(
                cardId = cardId,
                userId = userId
            )

            val result = firestoreDataSource.createCard(card)
            if (result.isSuccess) {
                Result.success(card)
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User?> {
        return firestoreDataSource.getUserByPhoneNumber(phoneNumber)
    }

    override fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUserId()
    }

    override fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }

    override fun signOut() {
        authDataSource.signOut()
    }
}