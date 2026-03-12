package com.modframework.ui

object PreferencesManager {
    private val prefs = mutableMapOf<String, Any>()

    fun setBoolean(key: String, value: Boolean) {
        prefs[key] = value
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs[key] as? Boolean ?: default
    }
}