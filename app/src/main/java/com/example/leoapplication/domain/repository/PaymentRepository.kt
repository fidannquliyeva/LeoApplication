import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.domain.model.Payment
import com.example.leoapplication.domain.model.Transaction
import com.example.leoapplication.domain.repository.TransactionRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PaymentRepository(
    private val firestore: FirebaseFirestore,
    private val bankCardRepositoryImpl: BankCardRepositoryImpl,
    private val transactionRepository: TransactionRepository
) {
    private val collection = firestore.collection("payments")

    suspend fun addPayment(payment: Payment): Boolean {
        return try {
            // 🔹 1. İstifadəçini yoxla
            val user = FirebaseAuth.getInstance().currentUser
                ?: return false // login olmayıbsa icazə vermirik

            // 🔹 2. Kart balansını yenilə
            val updateSuccess = bankCardRepositoryImpl.updateCardByNumber(
                payment.amount,
                payment.senderCardId.toString(),
                payment.receiverCardId?.toString()
            )
            if (!updateSuccess) return false

            // 🔹 3. Payment əlavə et
            collection.document(payment.id.toString()).set(payment).await()

            val transaction = Transaction(
                id = UUID.randomUUID().hashCode(), // random id
                transactionNumber = payment.paymentNumber,
                title = payment.paymentType.toString(),
                subtitle = payment.paymentTitle,
                amount = payment.amount,
                iconRes = 0,
                receiverCardId = payment.receiverCardId, // 🔹 indi String?
                senderCardId = payment.senderCardId,     // 🔹 String
                transactionDate = Timestamp.now(),
                transactionStatus = payment.paymentStatus.toString(),
                paymentId = payment.id,
                transactionIcon = "icon",
                userId = user.uid
            )


            transactionRepository.addTransaction(transaction)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
