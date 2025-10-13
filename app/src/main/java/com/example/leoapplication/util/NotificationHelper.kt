package com.example.leoapplication.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.leoapplication.R
import com.example.leoapplication.presentation.ui.fragments.home.MainActivity

/**
 * Notification helper - local notifications üçün
 */
object NotificationHelper {

    private const val CHANNEL_ID = "leobank_channel"
    private const val CHANNEL_NAME = "Leobank Notifications"
    private const val CHANNEL_DESCRIPTION = "Balans və transaction bildirişləri"

    /**
     * Notification channel yaratmaq (Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Balans artırma notification-u
     */
    fun showBalanceIncreaseNotification(context: Context, amount: Double) {
        val title = "Balans artırıldı"
        val message = "+${String.format("%.2f", amount)} ₼ balansınıza əlavə olundu"

        showNotification(context, title, message, 1)
    }

    /**
     * Transaction (transfer alındı) notification-u
     */
    fun showTransactionReceivedNotification(context: Context, amount: Double, description: String) {
        val title = "Pul alındı"
        val message = "+${String.format("%.2f", amount)} ₼\n$description"

        showNotification(context, title, message, 2)
    }

    /**
     * Transaction (transfer göndərildi) notification-u
     */
    fun showTransactionSentNotification(context: Context, amount: Double, description: String) {
        val title = "Pul göndərildi"
        val message = "-${String.format("%.2f", amount)} ₼\n$description"

        showNotification(context, title, message, 3)
    }

    /**
     * Generic notification göstərmək
     */
    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        // Intent - notification-a klikləyəndə MainActivity açılır
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Notification builder
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))

        // Notification göstər
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Notification permission-u yoxla (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true
        }
    }
}