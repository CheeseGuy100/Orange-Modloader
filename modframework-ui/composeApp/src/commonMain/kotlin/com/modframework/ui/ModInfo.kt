package com.modframework.ui

data class ModInfo(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean = true,
    val itemCount: Int = 0,
    val eventCount: Int = 0
)