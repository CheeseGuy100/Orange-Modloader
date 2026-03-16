package com.modframework.ui

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import java.io.File
import java.net.URL

@Serializable
data class ModrinthVersion(
    val files: List<ModrinthFile>
)

@Serializable
data class ModrinthFile(
    val url: String,
    val filename: String
)

suspend fun getModDownloadUrl(client: HttpClient, projectId: String): Pair<String, String>? {
    return try {
        val versions: List<ModrinthVersion> = client.get(
            "https://api.modrinth.com/v2/project/$projectId/version"
        ).body()
        val file = versions.firstOrNull()?.files?.firstOrNull()
        if (file != null) Pair(file.url, file.filename) else null
    } catch (e: Exception) {
        null
    }
}

fun downloadModFile(url: String, fileName: String) {
    try {
        val downloadsFolder = File(System.getProperty("user.home"), "Downloads")
        downloadsFolder.mkdirs()
        val outputFile = File(downloadsFolder, fileName)
        URL(url).openStream().use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}