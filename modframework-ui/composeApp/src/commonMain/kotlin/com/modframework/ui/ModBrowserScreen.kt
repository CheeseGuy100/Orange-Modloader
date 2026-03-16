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
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ModrinthMod(
    val project_id: String,
    val title: String,
    val author: String,
    val description: String,
    val categories: List<String>,
    val downloads: Int,
    val icon_url: String? = null
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
    val downloads: Int,
    val iconUrl: String? = null
)

enum class ModFilter { ALL, JAVA, BEDROCK }

@Composable
fun ModBrowserScreen(onBack: () -> Unit) {
    var mods by remember { mutableStateOf<List<BrowseMod>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf(ModFilter.ALL) }
    var downloadingModId by remember { mutableStateOf<String?>(null) }
    var downloadMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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

    suspend fun loadMods(filter: ModFilter) {
        isLoading = true
        error = null
        try {
            val facets = when (filter) {
                ModFilter.ALL -> "[[\"project_type:mod\"]]"
                ModFilter.JAVA -> "[[\"project_type:mod\"],[\"categories:forge\"]]"
                ModFilter.BEDROCK -> "[[\"project_type:mod\"],[\"categories:datapack\"]]"
            }
            val response: ModrinthResponse = client.get(
                "https://api.modrinth.com/v2/search?limit=20&facets=$facets"
            ).body()
            mods = response.hits.map { mod ->
                BrowseMod(
                    id = mod.project_id,
                    name = mod.title,
                    author = mod.author,
                    description = mod.description,
                    category = mod.categories.firstOrNull() ?: "Misc",
                    downloads = mod.downloads,
                    iconUrl = mod.icon_url
                )
            }
        } catch (e: Exception) {
            error = "Failed to load mods: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(selectedFilter) {
        loadMods(selectedFilter)
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

        if (downloadMessage != null) {
            Text(
                text = downloadMessage!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = Color(0xFF7CB342),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModFilter.values().forEach { filter ->
                val label = when (filter) {
                    ModFilter.ALL -> "🌐 All"
                    ModFilter.JAVA -> "☕ Java"
                    ModFilter.BEDROCK -> "🪨 Bedrock"
                }
                if (selectedFilter == filter) {
                    Button(
                        onClick = { selectedFilter = filter },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300)
                        )
                    ) {
                        Text(
                            label,
                            color = Color(0xFF7CB342),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedFilter = filter },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7CB342)
                        )
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("😭", style = MaterialTheme.typography.displayMedium)
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = { selectedFilter = selectedFilter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300)
                            )
                        ) {
                            Text("Retry", color = Color(0xFF7CB342))
                        }
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
                                        onClick = {
                                            scope.launch {
                                                downloadingModId = mod.id
                                                downloadMessage = "Downloading ${mod.name}..."
                                                val result = getModDownloadUrl(client, mod.id)
                                                if (result != null) {
                                                    downloadModFile(result.first, result.second)
                                                    downloadMessage = "✅ ${mod.name} downloaded to Downloads folder!"
                                                } else {
                                                    downloadMessage = "❌ Failed to get download URL"
                                                }
                                                downloadingModId = null
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (downloadingModId == mod.id) Color(0xFF558B2F) else Color(0xFFFFB300)
                                        ),
                                        enabled = downloadingModId == null
                                    ) {
                                        if (downloadingModId == mod.id) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color(0xFF7CB342),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                "Install",
                                                color = Color.White,
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
}