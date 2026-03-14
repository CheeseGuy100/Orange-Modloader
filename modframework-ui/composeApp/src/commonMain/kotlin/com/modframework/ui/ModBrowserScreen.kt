package com.modframework.ui

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
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ModrinthMod(
    val project_id: String,
    val title: String,
    val author: String,
    val description: String,
    val categories: List<String>,
    val downloads: Int
)

@Serializable
data class ModrinthResponse(
    val hits: List<ModrinthMod>
)

data class BrowseMod(
    val id: String,
    val name: String,
    val author: String,
    val description: String,
    val category: String,
    val downloads: Int
)

@Composable
fun ModBrowserScreen(onBack: () -> Unit) {
    var mods by remember { mutableStateOf<List<BrowseMod>>(emptyList()) }
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

    LaunchedEffect(Unit) {
        try {
            val response: ModrinthResponse = client.get("https://api.modrinth.com/v2/search?limit=20&facets=[[\"project_type:mod\"]]").body()
            mods = response.hits.map { mod ->
                BrowseMod(
                    id = mod.project_id,
                    name = mod.title,
                    author = mod.author,
                    description = mod.description,
                    category = mod.categories.firstOrNull() ?: "Misc",
                    downloads = mod.downloads
                )
            }
        } catch (e: Exception) {
            error = "Failed to load mods: ${e.message}"
        } finally {
            isLoading = false
        }
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
                text = "🥭 Mod Browser",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7CB342)
            )
        }

        HorizontalDivider()

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading mods from Modrinth...", color = Color(0xFF7CB342))
                    }
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😭", style = MaterialTheme.typography.displayMedium)
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mods) { mod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = mod.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7CB342)
                                    )
                                    Text(
                                        text = mod.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "by ${mod.author}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = mod.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⬇️ ${mod.downloads.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = { },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFB300)
                                        )
                                    ) {
                                        Text(
                                            "Install",
                                            color = Color(0xFF7CB342),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}