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
        ),

        TransactionType.TRANSFER to listOf(
            "köçürmə",
            "kocurme",
            "transfer",
            "köçür",
            "kocur"
        )
    )

    private val directionKeywords = mapOf(
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

    fun matchesQuery(
        transaction: Transaction,
        query: String,
        currentUserId: String
    ): Boolean {
        if (query.isBlank()) return true

        val searchQuery = query.lowercase().trim()

        val matchesDescription = transaction.description.lowercase().contains(searchQuery)
        val matchesAmount = transaction.amount.toString().contains(searchQuery)


        if (matchesDescription || matchesAmount) {
            return true
        }

        val typeKeywordsList = typeKeywords[transaction.type] ?: emptyList()
        val matchesTypeKeyword = typeKeywordsList.any { keyword ->
            keyword.startsWith(searchQuery) || searchQuery.startsWith(keyword)
        }


        if (matchesTypeKeyword) {
            return true
        }

        if (transaction.type == TransactionType.TRANSFER) {
            val isOutgoing = transaction.fromUserId == currentUserId
            val directionKey = if (isOutgoing) "outgoing" else "incoming"
            val directionKeywordsList = directionKeywords[directionKey] ?: emptyList()

            val matchesDirection = directionKeywordsList.any { keyword ->
                keyword.startsWith(searchQuery) || searchQuery.startsWith(keyword)
            }


            if (matchesDirection) {
                return true
            }
        }

        return false
    }
}