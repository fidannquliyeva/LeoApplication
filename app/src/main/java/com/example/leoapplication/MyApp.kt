package com.example.leoapplication

import android.app.Application
import com.example.leoapplication.util.LanguageManager
import com.example.leoapplication.util.NotificationHelper
import com.example.leoapplication.util.ThemeHelper
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        LanguageManager.loadLanguage(this)
//        ThemeHelper.applyTheme(this)
        NotificationHelper.createNotificationChannel(this)
    }
}