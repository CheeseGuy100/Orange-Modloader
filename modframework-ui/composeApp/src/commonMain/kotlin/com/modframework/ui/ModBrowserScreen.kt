package com.modframework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var searchQuery by remember { mutableStateOf("") }
    var selectedMod by remember { mutableStateOf<BrowseMod?>(null) }
    val scope = rememberCoroutineScope()
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

    val filteredMods = mods.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.author.contains(searchQuery, ignoreCase = true)
    }

    if (selectedMod != null) {
        ModDetailScreen(
            mod = selectedMod!!,
            onBack = { selectedMod = null }
        )
        return
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search mods...", color = Color(0xFF666666)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7CB342),
                unfocusedBorderColor = Color(0xFF2A2A2A),
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                cursorColor = Color(0xFF7CB342),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data class FilterOption(val filter: ModFilter, val label: String, val icon: ImageVector)
            val filters = listOf(
                FilterOption(ModFilter.ALL, "All", Icons.Default.Language),
                FilterOption(ModFilter.JAVA, "Java", Icons.Default.Coffee),
                FilterOption(ModFilter.BEDROCK, "Bedrock", Icons.Default.Terrain)
            )
            filters.forEach { option ->
                if (selectedFilter == option.filter) {
                    Button(
                        onClick = { selectedFilter = option.filter },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300)
                        )
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.label,
                            tint = Color(0xFF7CB342),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            option.label,
                            color = Color(0xFF7CB342),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedFilter = option.filter },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7CB342)
                        )
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.label,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(option.label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
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
                        Text("😕", fontSize = 48.sp, color = Color.Gray)
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = { selectedFilter = selectedFilter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300)
                            )
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }
            filteredMods.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("😕", fontSize = 48.sp, color = Color.Gray)
                        Text(
                            "No mods found",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMods) { mod ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMod = mod },
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
                                                downloadMessage = "Getting download link..."
                                                val result = getModDownloadUrl(client, mod.id)
                                                if (result != null) {
                                                    uriHandler.openUri(result.first)
                                                    downloadMessage = "✅ Opening download for ${mod.name}"
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
                                                color = Color.White,
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