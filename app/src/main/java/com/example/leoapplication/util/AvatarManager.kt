package com.example.leoapplication.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object AvatarManager {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_AVATAR_PATH = "avatar_path"
    private const val AVATAR_FILE_NAME = "user_avatar.jpg"


    fun saveAvatar(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream == null) {
                return
            }

            // Internal storage-da fayl yarat
            val avatarFile = File(context.filesDir, AVATAR_FILE_NAME)

            // Şəkli kopyala
            val outputStream = FileOutputStream(avatarFile)
            inputStream.copyTo(outputStream)

            // Stream-ləri bağla
            inputStream.close()
            outputStream.close()

            // Fayl path-ini SharedPreferences-də saxla
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_AVATAR_PATH, avatarFile.absolutePath)
                .apply()


        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Avatar save failed: ${e.message}", e)
        }
    }


    fun getAvatar(context: Context): String? {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val path = prefs.getString(KEY_AVATAR_PATH, null)

            if (path != null) {
                val file = File(path)

                if (file.exists()) {
                    return path
                } else {
                    clearAvatar(context)
                }
            }

        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Get avatar failed: ${e.message}", e)
        }

        return null
    }

    fun clearAvatar(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val path = prefs.getString(KEY_AVATAR_PATH, null)

            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    Log.d("AvatarManager", "✅ Avatar file deleted")
                }
            }

            prefs.edit().remove(KEY_AVATAR_PATH).apply()


        } catch (e: Exception) {
            Log.e("AvatarManager", "❌ Clear avatar failed: ${e.message}", e)
        }
    }

    fun hasAvatar(context: Context): Boolean {
        return getAvatar(context) != null
    }
}