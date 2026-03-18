package com.modframework.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
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
data class ModrinthProject(
    val title: String,
    val description: String,
    val body: String,
    val downloads: Int,
    val categories: List<String>,
    val icon_url: String? = null,
    val source_url: String? = null,
    val versions: List<String> = emptyList()
)

@Composable
fun ModDetailScreen(
    mod: BrowseMod,
    onBack: () -> Unit
) {
    var project by remember { mutableStateOf<ModrinthProject?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showVersionPicker by remember { mutableStateOf(false) }
    var downloadMessage by remember { mutableStateOf<String?>(null) }
    val uriHandler = LocalUriHandler.current

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
            project = client.get("https://api.modrinth.com/v2/project/${mod.id}").body()
        } catch (e: Exception) {
            // use mod data as fallback
        } finally {
            isLoading = false
        }
    }

    if (showVersionPicker) {
        VersionPickerDialog(
            mod = mod,
            onDismiss = { showVersionPicker = false },
            onVersionSelected = { version ->
                val file = version.files.firstOrNull()
                if (file != null) {
                    uriHandler.openUri(file.url)
                    downloadMessage = "✅ Opening download for ${mod.name} ${version.version_number}"
                }
                showVersionPicker = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFF7CB342))
            }
            Text(
                text = mod.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7CB342)
            )
        }

        HorizontalDivider()

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFFB300))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF2A2A2A)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🥭", fontSize = 32.sp)
                                }
                            }

                            Column {
                                Text(
                                    text = mod.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7CB342)
                                )
                                Text(
                                    text = "by ${mod.author}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "⬇️ ${mod.downloads.toString().reversed().chunked(3).joinToString(",").reversed()} downloads",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            project?.categories?.take(3)?.forEach { category ->
                                Card(
                                    shape = RoundedCornerShape(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1B2E00)
                                    )
                                ) {
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF7CB342)
                                    )
                                }
                            }
                        }
                    }
                }

                if (downloadMessage != null) {
                    Text(
                        text = downloadMessage!!,
                        color = Color(0xFF7CB342),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = { showVersionPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300)
                    )
                ) {
                    Text(
                        "⬇️ Install Mod",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7CB342)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project?.body ?: mod.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}