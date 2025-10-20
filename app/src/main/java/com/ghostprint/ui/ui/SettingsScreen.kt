package com.ghostprint.ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.ghostprint.ui.domain.SettingsStore

@Composable
fun SettingsScreen(
    settings: SettingsStore
) {
    val scope = rememberCoroutineScope()
    var consent by remember { mutableStateOf(settings.isConsentEnabled()) }
    var paused by remember { mutableStateOf(settings.isLoggingPaused()) }
    var threshold by remember { mutableStateOf(settings.getThreshold()) }

    Column(Modifier.padding(16.dp)) {
        Text("GhostPrint Settings", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Consent enabled")
            Switch(checked = consent, onCheckedChange = {
                consent = it
                scope.launch { settings.setConsentEnabled(it) }
            })
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Logging paused")
            Switch(checked = paused, onCheckedChange = {
                paused = it
                scope.launch { settings.setLoggingPaused(it) }
            })
        }

        Spacer(Modifier.height(12.dp))
        Text("Anomaly threshold: $threshold")
        Slider(
            value = threshold,
            onValueChange = { threshold = it },
            valueRange = 0.5f..3.0f
        )
        Button(onClick = { scope.launch { settings.setThreshold(threshold) } }) {
            Text("Save threshold")
        }
    }
}