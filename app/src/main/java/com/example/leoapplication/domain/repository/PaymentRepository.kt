package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.domain.model.Payment
import com.example.leoapplication.domain.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class PaymentRepository(private val firestore: FirebaseFirestore,
                        private val bankCardRepositoryImpl: BankCardRepositoryImpl,
                        private val transactionRepository: TransactionRepository) {
    val collection = firestore.collection("payments")

    suspend fun addPayment(payment: Payment,onResult: (Boolean) -> Unit){
        bankCardRepositoryImpl.updateCardByNumber(payment.amount,payment.senderCardId.toString(),
            payment.receiverCardId.toString()) {
            if (!it) {
                onResult(false)
                return@updateCardByNumber
            }
        }

        collection.document(payment.id.toString())
            .set(payment)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
        transactionRepository.addTransaction(Transaction(
            id = 1,
            transactionNumber = payment.paymentNumber,
            title = payment.paymentType.toString(),
            subtitle = payment.paymentTitle,
            amount = payment.amount,
            iconRes = 13123,
            receiverCardId = payment.receiverCardId ?: 0,
            senderCardId = payment.senderCardId ?: 0,
            transactionDate = payment.paymentDate,
            transactionStatus = payment.paymentStatus.toString(),
            paymentId = payment.id,
            transactionIcon = "2312"
        )














        ) {

        }




    }
}