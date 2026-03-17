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

expect fun downloadModFile(url: String, fileName: String)
Then create androidMain/kotlin/com/modframework/ui/ModDownloader.kt:
package com.modframework.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri

actual fun downloadModFile(url: String, fileName: String) {
    val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(fileName)
        .setDescription("Downloading mod...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir("Downloads", fileName)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
    dm.enqueue(request)
}