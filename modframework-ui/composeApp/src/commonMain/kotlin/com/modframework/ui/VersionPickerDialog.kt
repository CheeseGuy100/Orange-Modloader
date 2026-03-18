package com.modframework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ModVersion(
    val id: String,
    val name: String,
    val version_number: String,
    val game_versions: List<String>,
    val loaders: List<String>,
    val files: List<ModrinthFile>,
    val downloads: Int
)

@Composable
fun VersionPickerDialog(
    mod: BrowseMod,
    onDismiss: () -> Unit,
    onVersionSelected: (ModVersion) -> Unit
) {
    var versions by remember { mutableStateOf<List<ModVersion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val client = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { client.close() }
    }

    LaunchedEffect(mod.id) {
        try {
            versions = client.get(
                "https://api.modrinth.com/v2/project/${mod.id}/version"
            ).body()
        } catch (e: Exception) {
            error = "Failed to load versions"
        } finally {
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Select Version",
                color = Color(0xFF7CB342),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFFB300))
                    }
                }
                error != null -> {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.height(400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(versions) { version ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onVersionSelected(version) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF252525)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = version.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7CB342),
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = version.version_number,
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        version.loaders.take(2).forEach { loader ->
                                            Card(
                                                shape = RoundedCornerShape(4.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFF1B2E00)
                                                )
                                            ) {
                                                Text(
                                                    text = loader,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    color = Color(0xFF7CB342),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        version.game_versions.take(2).forEach { gameVer ->
                                            Card(
                                                shape = RoundedCornerShape(4.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFF2D1E00)
                                                )
                                            ) {
                                                Text(
                                                    text = gameVer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    color = Color(0xFFFFB300),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "⬇️ ${version.downloads} downloads",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF7CB342))
            }
        }
    )
}