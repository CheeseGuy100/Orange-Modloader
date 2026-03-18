package com.modframework.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.Coil
import coil.ImageLoader

lateinit var appContext: Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this
        PreferencesManager.init(this)
        Coil.setImageLoader(
            ImageLoader.Builder(this).build()
        )
        setContent {
            App()
        }
    }
}