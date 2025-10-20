// ui/ConsentScreen.kt
package com.ghostprint.ui.ui

import android.provider.Settings
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConsentScreen(
    isServiceEnabled: Boolean,
    onOpenAccessibility: () -> Unit,
    onContinue: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("GhostPrint needs your explicit consent to observe system interaction events to improve device security.")
        Spacer(Modifier.height(8.dp))
        Text("What is collected: event types (scroll, click, text changes, hardware keys), timing metadata. You can disable anytime.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenAccessibility) {
            Text("Open Accessibility Settings")
        }
        Spacer(Modifier.height(8.dp))
        if (isServiceEnabled) {
            Button(onClick = onContinue) {
                Text("Continue")
            }
        } else {
            Text("Enable 'GhostPrint Accessibility Service' in settings to proceed.", style = MaterialTheme.typography.bodySmall)
        }
    }
}