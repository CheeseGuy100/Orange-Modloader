package com.modframework.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.modframework.ui.components.ModCard
import com.modframework.ui.components.ModDetailPanel
import com.modframework.ui.viewmodel.ModViewModel

@Composable
fun App(viewModel: ModViewModel = remember { ModViewModel() }) {
    OrangeModLoaderTheme {
        val mods by viewModel.mods.collectAsState()
        val selectedMod by viewModel.selectedMod.collectAsState()

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
                            onDisableAll = { viewModel.disableAll() }
                        )
                        ModList(
                            mods = mods,
                            selectedModId = selectedMod?.id,
                            onSelect = { viewModel.selectMod(it) },
                            onToggle = { viewModel.toggleMod(it) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
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
                        onDisableAll = { viewModel.disableAll() }
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

@Composable
private fun ModListHeader(
    enabledCount: Int,
    totalCount: Int,
    onEnableAll: () -> Unit,
    onDisableAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "🟠 Orange ModLoader",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$enabledCount / $totalCount mods active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDisableAll,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("None", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = onEnableAll,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("All", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        LinearProgressIndicator(
            progress = { if (totalCount > 0) enabledCount.toFloat() / totalCount else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}