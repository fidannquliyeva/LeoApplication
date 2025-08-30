package com.example.leoapplication.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leoapplication.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncreaseBalanceOtherCardsFragment : Fragment() {

    private lateinit var cardNumber: String
    private lateinit var cvv: String
    private lateinit var mm: String
    private lateinit var yy: String

    private var myCardRef: DocumentReference? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cardNumber = it.getString("cardNumber", "")
            cvv = it.getString("cvv", "")
            mm = it.getString("mm", "")
            yy = it.getString("yy", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_increase_balance_other_cards, container, false)

        val amountInput = view.findViewById<EditText>(R.id.phoneNumberText)
        val nextButton = view.findViewById<MaterialButton>(R.id.nextButton)

        // Dinamik ownerPhone SharedPreferences-dən
        val sharedPref = requireContext().getSharedPreferences("user_prefs", 0)
        val ownerPhone = sharedPref.getString("ownerPhone", "") ?: ""

        fetchMyCard(ownerPhone)

        nextButton.setOnClickListener {
            val amountText = amountInput.text.toString()
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Düzgün məbləğ daxil edin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (myCardRef == null) {
                Toast.makeText(context, "Sənin kartın hələ yüklənməyib!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            transferFromOtherCardToMine(amount)
        }

        return view
    }

    private fun fetchMyCard(ownerPhone: String) {
        db.collection("bankCards")
            .whereEqualTo("ownerPhone", ownerPhone)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    myCardRef = documents.documents[0].reference
                } else {
                    Toast.makeText(context, "Sənin kartın tapılmadı!", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Xəta baş verdi: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun transferFromOtherCardToMine(amount: Double) {
        val fromCardQuery = db.collection("bankCards")
            .whereEqualTo("cardNumber", cardNumber)
            .whereEqualTo("cvv", cvv)
            .whereEqualTo("mm", mm)
            .whereEqualTo("yy", yy)

        // Digər kartı tapırıq
        fromCardQuery.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                Toast.makeText(context, "Digər kart tapılmadı!", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val fromCardRef = querySnapshot.documents[0].reference

            // Transaction ilə balansları dəyişirik
            db.runTransaction { transaction ->
                val fromCardSnap = transaction.get(fromCardRef)
                val fromBalance = fromCardSnap.getDouble("balance") ?: 0.0
                if (fromBalance < amount) throw Exception("Kifayət qədər balans yoxdur!")

                // Digər kartdan azaldırıq
                transaction.update(fromCardRef, "balance", fromBalance - amount)

                // Öz kartımıza əlavə edirik
                val mySnap = transaction.get(myCardRef!!)
                val myBalance = mySnap.getDouble("balance") ?: 0.0
                transaction.update(myCardRef!!, "balance", myBalance + amount)
            }.addOnSuccessListener {
                Toast.makeText(context, "Transfer uğurla tamamlandı!", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Xəta baş verdi: ${e.message}", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener { e ->
            Toast.makeText(context, "Xəta baş verdi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
