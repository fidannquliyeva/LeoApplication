package com.example.leoapplication.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Tema idarəetməsi - sadə versiya
 */
object ThemeHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DARK_THEME = "dark_theme"

    /**
     * Qaranlıq tema aktiv?
     */
    fun isDarkTheme(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_THEME, false)
    }

    /**
     * Temanı saxla
     */
    fun setTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }

    /**
     * Temanı tətbiq et
     */
    fun applyTheme(context: Context) {
        val isDark = isDarkTheme(context)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}