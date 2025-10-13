package com.example.leoapplication.util

import android.util.Log
import com.example.leoapplication.data.model.Transaction
import com.example.leoapplication.data.model.TransactionType

object TransactionSearchHelper {

    private val typeKeywords = mapOf(
        TransactionType.BALANCE_INCREASE to listOf(
            "balans artırma",
            "balans artirma",
            "balans",
            "artırma",
            "artirma",
            "increase",
            "balance",
            "top up",
            "yükləmə",
            "yukleme"
        ),

        TransactionType.PAYMENT to listOf(
            "ödəniş",
            "odenish",
            "ödən",
            "oden",
            "payment",
            "pay"
        ),

        TransactionType.DEPOSIT to listOf(
            "depozit",
            "deposit",
            "yatırım",
            "yatirim"
        ),

        TransactionType.WITHDRAWAL to listOf(
            "çıxarış",
            "cixarish",
            "çıx",
            "cix",
            "withdrawal",
            "withdraw",
            "nağdlaşdırma",
            "nagdlasdirma"
        )

    )



    private val transferKeywords = mapOf(
        "common" to listOf(
            "köçürmə",
            "kocurme",
            "transfer",
            "köçür",
            "kocur"
        ),
        "outgoing" to listOf(
            "göndərildi",
            "gonderildi",
            "göndər",
            "gonder",
            "sent",
            "outgoing",
            "send"
        ),
        "incoming" to listOf(
            "alındı",
            "alindi",
            "alın",
            "alin",
            "received",
            "incoming",
            "receive"
        )
    )

    /**
     * Transaction üçün bütün axtarış keyword-lərini gətirir
     */
    fun getSearchKeywords(transaction: Transaction, currentUserId: String): List<String> {
        val keywords = mutableListOf<String>()

        keywords.add(transaction.description.lowercase())
        keywords.add(transaction.amount.toString())
        keywords.add(transaction.currency.lowercase())

        keywords.add(transaction.fromCardId.lowercase())
        keywords.add(transaction.toCardId.lowercase())

        when (transaction.type) {
            TransactionType.TRANSFER -> {

                keywords.addAll(transferKeywords["common"] ?: emptyList())


                val isOutgoing = transaction.fromUserId == currentUserId
                val directionKey = if (isOutgoing) "outgoing" else "incoming"
                keywords.addAll(transferKeywords[directionKey] ?: emptyList())
            }

            else -> {

                keywords.addAll(typeKeywords[transaction.type] ?: emptyList())
            }
        }

        return keywords
    }

    /**
     * Transaction query ilə uyğun gəlirmi?
     */

    fun matchesQuery(
        transaction: Transaction,
        query: String,
        currentUserId: String
    ): Boolean {
        if (query.isBlank()) return true

        val searchQuery = query.lowercase().trim()
        val keywords = getSearchKeywords(transaction, currentUserId)


        // ✅ DEBUG LOG
        Log.d("SearchHelper", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d("SearchHelper", "Query: '$searchQuery'")
        Log.d("SearchHelper", "Transaction: [${transaction.type}] ${transaction.amount}")
        Log.d("SearchHelper", "Keywords: $keywords")
        return keywords.any { keyword ->
            keyword.contains(searchQuery)
        }
    }


    /**
     * YENİ TYPE ƏLAVƏ ETMƏK üçün helper metod
     */
    fun addCustomKeywords(type: TransactionType, keywords: List<String>) {
        // Bu metod gələcəkdə runtime-da keyword əlavə etmək üçün
        // İndilik static map istifadə edirik
    }
}