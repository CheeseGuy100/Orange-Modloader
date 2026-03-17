package com.modframework.ui

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

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