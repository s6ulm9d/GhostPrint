package com.ghostprint.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Channels {
    const val LOGGING = "gp_logging"
    const val ALERTS = "gp_alerts"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(LOGGING, "GhostPrint Logging", NotificationManager.IMPORTANCE_MIN)
            )
            nm.createNotificationChannel(
                NotificationChannel(ALERTS, "GhostPrint Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }
}