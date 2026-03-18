package com.modframework.ui

import android.content.Context
import android.content.SharedPreferences

actual object PreferencesManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("mangoloader_prefs", Context.MODE_PRIVATE)
    }

    actual fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }
}