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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import org.json.JSONObject

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

    LaunchedEffect(Unit) {
        try {
            val result = withContext(Dispatchers.IO) {
                val url = URL("https://api.modrinth.com/v2/search?limit=20&facets=[[%22project_type:mod%22]]")
                val response = url.readText()
                val json = JSONObject(response)
                val hits = json.getJSONArray("hits")
                val list = mutableListOf<BrowseMod>()
                for (i in 0 until hits.length()) {
                    val hit = hits.getJSONObject(i)
                    val categories = hit.getJSONArray("categories")
                    val category = if (categories.length() > 0) categories.getString(0) else "Misc"
                    list.add(
                        BrowseMod(
                            id = hit.getString("project_id"),
                            name = hit.getString("title"),
                            author = hit.getString("author"),
                            description = hit.getString("description"),
                            category = category,
                            downloads = hit.getInt("downloads")
                        )
                    )
                }
                list
            }
            mods = result
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