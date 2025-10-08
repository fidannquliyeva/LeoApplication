package com.example.leoapplication.util

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

object PinManager {

    private const val PREFS_NAME = "pin_prefs"
    private const val KEY_PIN = "user_pin"
    private const val KEY_PIN_SET = "is_pin_set"
    private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
    private const val KEY_LOCK_TIME = "lock_time"

    private const val MAX_ATTEMPTS = 3
    private const val LOCK_DURATION = 5 * 60 * 1000L // 5 dəqiqə

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * PIN-i hash-ləyir (təhlükəsizlik üçün)
     */
    private fun hashPin(pin: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    /**
     * PIN-i saxlamaq
     */
    fun savePin(context: Context, pin: String) {
        getPrefs(context).edit().apply {
            putString(KEY_PIN, hashPin(pin))
            putBoolean(KEY_PIN_SET, true)
            putInt(KEY_FAILED_ATTEMPTS, 0)
            putLong(KEY_LOCK_TIME, 0)
            apply()
        }
    }

    /**
     * PIN-i oxumaq
     */
    private fun getPin(context: Context): String? {
        return getPrefs(context).getString(KEY_PIN, null)
    }

    /**
     * PIN təyin edilib?
     */
    fun isPinSet(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_PIN_SET, false)
    }

    /**
     * PIN yoxlama
     */
    fun verifyPin(context: Context, inputPin: String): Boolean {
        val savedPin = getPin(context)
        return savedPin == hashPin(inputPin)
    }

    /**
     * Səhv cəhd əlavə et
     */
    fun addFailedAttempt(context: Context) {
        val prefs = getPrefs(context)
        val attempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply()

        if (attempts >= MAX_ATTEMPTS) {
            lockUser(context)
        }
    }

    /**
     * İstifadəçini blokla
     */
    private fun lockUser(context: Context) {
        val lockTime = System.currentTimeMillis() + LOCK_DURATION
        getPrefs(context).edit().putLong(KEY_LOCK_TIME, lockTime).apply()
    }

    /**
     * İstifadəçi bloklanıb?
     */
    fun isUserLocked(context: Context): Boolean {
        val lockTime = getPrefs(context).getLong(KEY_LOCK_TIME, 0)
        return System.currentTimeMillis() < lockTime
    }

    /**
     * Qalan lock vaxtı (saniyə)
     */
    fun getRemainingLockTime(context: Context): Long {
        val lockTime = getPrefs(context).getLong(KEY_LOCK_TIME, 0)
        val remaining = lockTime - System.currentTimeMillis()
        return if (remaining > 0) remaining / 1000 else 0
    }

    /**
     * Səhv cəhd sayını sıfırla
     */
    fun resetFailedAttempts(context: Context) {
        getPrefs(context).edit().apply {
            putInt(KEY_FAILED_ATTEMPTS, 0)
            putLong(KEY_LOCK_TIME, 0)
            apply()
        }
    }

    /**
     * Qalan cəhd sayı
     */
    fun getRemainingAttempts(context: Context): Int {
        val attempts = getPrefs(context).getInt(KEY_FAILED_ATTEMPTS, 0)
        return (MAX_ATTEMPTS - attempts).coerceAtLeast(0)
    }

    /**
     * PIN-i silmək (çıxış zamanı)
     */
    fun clearPin(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}