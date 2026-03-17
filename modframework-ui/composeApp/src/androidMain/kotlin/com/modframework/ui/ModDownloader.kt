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