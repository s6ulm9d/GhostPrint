package com.ghostprint.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ghostprint.ui.domain.SettingsStore
import com.ghostprint.ui.service.LoggerForegroundService
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("MainActivity", "POST_NOTIFICATIONS granted=$granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val settingsStore = SettingsStore(this)

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    val scope = rememberCoroutineScope()
                    var consent by remember { mutableStateOf(settingsStore.isConsentEnabled()) }
                    var paused by remember { mutableStateOf(settingsStore.isLoggingPaused()) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(16.dp)
                    ) {
                        Text("GhostPrint Controls", color = Color.White)
                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consent enabled", color = Color.White)
                            Switch(checked = consent, onCheckedChange = {
                                consent = it
                                scope.launch { settingsStore.setConsentEnabled(it) }
                            })
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Logging paused", color = Color.White)
                            Switch(checked = paused, onCheckedChange = {
                                paused = it
                                scope.launch { settingsStore.setLoggingPaused(it) }
                            })
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = { startLoggerService() }) { Text("Start Service") }
                            Button(onClick = { stopLoggerService() }) { Text("Stop Service") }
                            Button(onClick = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }) {
                                Text("Open Accessibility")
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Enable service in Accessibility, then toggle Consent ON and Logging Paused OFF to start capturing.",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    private fun startLoggerService() {
        val intent = Intent(this, LoggerForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
        Log.d("MainActivity", "LoggerForegroundService started")
    }

    private fun stopLoggerService() {
        val intent = Intent(this, LoggerForegroundService::class.java)
        stopService(intent)
        Log.d("MainActivity", "LoggerForegroundService stopped")
    }
}