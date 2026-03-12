package com.modframework.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var tapCount by remember { mutableStateOf(0) }
    var showCheese by remember { mutableStateOf(false) }

    LaunchedEffect(tapCount) {
        if (tapCount >= 5) {
            showCheese = true
            delay(3000)
            showCheese = false
            tapCount = 0
        }
    }

    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Back")
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B00)
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Toggle dark or light theme",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFF6B00),
                        checkedTrackColor = Color(0xFFFF6B00).copy(alpha = 0.5f)
                    )
                )
            }

            HorizontalDivider()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B00)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Orange ModLoader",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Version 0.2.0-Alpha",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { tapCount++ }
                )
                Text(
                    text = "Built by CheeseGuy100",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "UI/UX by Claude (Anthropic)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MIT License - Open Source",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B00)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "☕ ko-fi.com/cheeseguycheese23983",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF6B00),
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://ko-fi.com/cheeseguycheese23983")
                    }
                )
            }
        }

        if (showCheese) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                    Text(
                        text = "CheeseGuy100 was here 🧀",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B00)
                    )
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                    Text("🧀🧀🧀🧀🧀", fontSize = 40.sp)
                }
            }
        }
    }
}