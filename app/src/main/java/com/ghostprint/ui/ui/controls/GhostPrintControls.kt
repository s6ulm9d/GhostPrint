package com.ghostprint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ghostprint.ui.domain.SettingsStore
import kotlinx.coroutines.launch

@Composable
fun GhostPrintControls(
    settingsStore: SettingsStore,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onOpenAccessibility: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var consent by remember { mutableStateOf(settingsStore.isConsentEnabled()) }
    var paused by remember { mutableStateOf(settingsStore.isLoggingPaused()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("GhostPrint Controls", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Consent enabled")
            Switch(
                checked = consent,
                onCheckedChange = {
                    consent = it
                    scope.launch { settingsStore.setConsentEnabled(it) }
                }
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Logging paused")
            Switch(
                checked = paused,
                onCheckedChange = {
                    paused = it
                    scope.launch { settingsStore.setLoggingPaused(it) }
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onStartService) { Text("Start Service") }
            Button(onClick = onStopService) { Text("Stop Service") }
            Button(onClick = onOpenAccessibility) { Text("Open Accessibility") }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Enable service in Accessibility, then toggle Consent ON and Logging Paused OFF to start capturing.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}