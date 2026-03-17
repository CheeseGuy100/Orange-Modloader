package com.modframework.ui

import java.io.File
import java.net.URL

actual fun downloadModFile(url: String, fileName: String) {
    val downloadsFolder = File(System.getProperty("user.home"), "Downloads")
    downloadsFolder.mkdirs()
    val outputFile = File(downloadsFolder, fileName)
    URL(url).openStream().use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}
