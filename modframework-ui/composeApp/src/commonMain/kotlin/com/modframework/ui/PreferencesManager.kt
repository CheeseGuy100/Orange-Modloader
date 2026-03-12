package com.modframework.ui

import com.russhwolf.settings.Settings

object PreferencesManager {
    private val settings = Settings()

    fun setBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return settings.getBoolean(key, default)
    }
}