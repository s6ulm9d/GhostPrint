// ui/StatusScreen.kt
package com.ghostprint.ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusScreen(
    loggingEnabled: Boolean,
    threshold: Float,
    onToggleLogging: (Boolean) -> Unit,
    onThresholdChange: (Float) -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("GhostPrint status")
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Logging")
            Spacer(Modifier.width(12.dp))
            Switch(checked = loggingEnabled, onCheckedChange = onToggleLogging)
        }
        Spacer(Modifier.height(16.dp))
        Text("Anomaly threshold: ${"%.2f".format(threshold)}")
        Slider(
            value = threshold,
            onValueChange = onThresholdChange,
            valueRange = 0.1f..5.0f,
            steps = 10
        )
        Spacer(Modifier.height(8.dp))
        Text("Higher values = less sensitive. Lower values = more sensitive.")
    }
}