package com.modframework.ui

expect object PreferencesManager {
    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean = false): Boolean
}