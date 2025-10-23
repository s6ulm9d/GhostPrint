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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostprint.ui.domain.SettingsStore
import com.ghostprint.ui.navigation.NavRoutes
import com.ghostprint.ui.service.LoggerForegroundService
import com.ghostprint.ui.ui.logs.LogsScreen
import com.ghostprint.ui.ui.logs.LogsViewModel

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
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = navController.currentDestination?.route == NavRoutes.Controls.route,
                                onClick = { navController.navigate(NavRoutes.Controls.route) },
                                label = { Text("Controls") },
                                icon = {}
                            )
                            NavigationBarItem(
                                selected = navController.currentDestination?.route == NavRoutes.Logs.route,
                                onClick = { navController.navigate(NavRoutes.Logs.route) },
                                label = { Text("Logs") },
                                icon = {}
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Controls.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(NavRoutes.Controls.route) {
                            // GhostPrintControls must be in the same package (com.ghostprint.ui)
                            // or imported from its package. If not yet created, add it under com.ghostprint.ui.
                            GhostPrintControls(
                                settingsStore = settingsStore,
                                onStartService = { startLoggerService() },
                                onStopService = { stopLoggerService() },
                                onOpenAccessibility = {
                                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                }
                            )
                        }
                        composable(NavRoutes.Logs.route) {
                            val dao = (application as GhostPrintApp).db.logEventDao()
                            val viewModel = LogsViewModel(dao)
                            LogsScreen(viewModel)
                        }
                    }
                }
            }
        }
    }

    private fun startLoggerService() {
        val intent = Intent(this, LoggerForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        Log.d("MainActivity", "LoggerForegroundService started")
    }

    private fun stopLoggerService() {
        val intent = Intent(this, LoggerForegroundService::class.java)
        stopService(intent)
        Log.d("MainActivity", "LoggerForegroundService stopped")
    }
}