package com.modframework.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.modframework.ui.components.ModCard
import com.modframework.ui.components.ModDetailPanel
import com.modframework.ui.viewmodel.ModViewModel

@Composable
fun App(viewModel: ModViewModel = remember { ModViewModel() }) {
    var isDarkMode by remember { mutableStateOf(PreferencesManager.getBoolean("darkMode", true)) }
    var showSettings by remember { mutableStateOf(false) }
    var showBrowser by remember { mutableStateOf(false) }

    MangoLoaderTheme(darkTheme = isDarkMode) {
        val mods by viewModel.mods.collectAsState()
        val selectedMod by viewModel.selectedMod.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        if (showBrowser) {
            ModBrowserScreen(onBack = { showBrowser = false })
        } else if (showSettings) {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeToggle = {
                    isDarkMode = it
                    PreferencesManager.setBoolean("darkMode", it)
                },
                onBack = { showSettings = false }
            )
        } else if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = Color(0xFFFFB300),
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val isWideScreen = maxWidth > 600.dp

                if (isWideScreen) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .width(360.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            ModListHeader(
                                enabledCount = viewModel.enabledCount,
                                totalCount = viewModel.totalCount,
                                onEnableAll = { viewModel.enableAll() },
                                onDisableAll = { viewModel.disableAll() },
                                onSettings = { showSettings = true },
                                onBrowser = { showBrowser = true }
                            )
                            ModList(
                                mods = mods,
                                selectedModId = selectedMod?.id,
                                onSelect = { viewModel.selectMod(it) },
                                onToggle = { viewModel.toggleMod(it) }
                            )
                        }
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (selectedMod != null) {
                                ModDetailPanel(
                                    mod = selectedMod!!,
                                    onToggle = { viewModel.toggleMod(selectedMod!!.id) }
                                )
                            } else {
                                EmptyDetailState()
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ModListHeader(
                            enabledCount = viewModel.enabledCount,
                            totalCount = viewModel.totalCount,
                            onEnableAll = { viewModel.enableAll() },
                            onDisableAll = { viewModel.disableAll() },
                            onSettings = { showSettings = true },
                            onBrowser = { showBrowser = true }
                        )
                        if (selectedMod != null) {
                            Column {
                                TextButton(
                                    onClick = { viewModel.selectMod(null) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("← Back to mods")
                                }
                                ModDetailPanel(
                                    mod = selectedMod!!,
                                    onToggle = { viewModel.toggleMod(selectedMod!!.id) }
                                )
                            }
                        } else {
                            ModList(
                                mods = mods,
                                selectedModId = null,
                                onSelect = { viewModel.selectMod(it) },
                                onToggle = { viewModel.toggleMod(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModListHeader(
    enabledCount: Int,
    totalCount: Int,
    onEnableAll: () -> Unit,
    onDisableAll: () -> Unit,
    onSettings: () -> Unit,
    onBrowser: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🥭 MangoLoader",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7CB342)
                )
                Text(
                    text = "$enabledCount / $totalCount mods active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = onDisableAll,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF7CB342)
                    )
                ) {
                    Text("None", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = onEnableAll,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color(0xFF1A1000)
                    )
                ) {
                    Text("All", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7CB342))
                }
                IconButton(onClick = onBrowser) {
                    Text("🥭")
                }
                IconButton(onClick = onSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF7CB342)
                    )
                }
            }
        }
        LinearProgressIndicator(
            progress = { if (totalCount > 0) enabledCount.toFloat() / totalCount else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF7CB342),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

@Composable
private fun ModList(
    mods: List<ModInfo>,
    selectedModId: String?,
    onSelect: (ModInfo) -> Unit,
    onToggle: (String) -> Unit
) {
    if (mods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📭", style = MaterialTheme.typography.displayMedium)
                Text(
                    text = "Sorry, there aren't any mods here",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF7CB342)
                )
                Text(
                    text = "Add mods to get started!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mods, key = { it.id }) { mod ->
                ModCard(
                    mod = mod,
                    isSelected = mod.id == selectedModId,
                    onClick = { onSelect(mod) },
                    onToggle = { onToggle(mod.id) }
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun EmptyDetailState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "📦", style = MaterialTheme.typography.displayMedium)
            Text(
                text = "Select a mod to view details",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF7CB342)
            )
        }
    }
}