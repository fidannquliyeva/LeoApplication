package com.example.leoapplication.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Toast göstərmə
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

/**
 * Telefon nömrəsini formatlamaq
 * Input: "501234567"
 * Output: "+994 50 123 45 67"
 */
fun String.formatPhoneNumber(): String {
    if (this.length != Constants.PHONE_NUMBER_LENGTH) return this

    return buildString {
        append(Constants.COUNTRY_CODE)
        append(" ")
        append(this@formatPhoneNumber.substring(0, 2))
        append(" ")
        append(this@formatPhoneNumber.substring(2, 5))
        append(" ")
        append(this@formatPhoneNumber.substring(5, 7))
        append(" ")
        append(this@formatPhoneNumber.substring(7, 9))
    }
}

/**
 * Email validasiyası
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * FIN kod validasiyası
 */
fun String.isValidFinCode(): Boolean {
    return this.length == Constants.FIN_CODE_LENGTH && this.all { it.isLetterOrDigit() }
}

/**
 * Boş və ya null yoxlama
 */
fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}