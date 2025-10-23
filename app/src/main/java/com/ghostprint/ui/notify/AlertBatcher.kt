package com.ghostprint.ui.notify

import android.content.Context
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue

class AlertBatcher(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val queue = ConcurrentLinkedQueue<String>()
    @Volatile private var scheduled = false

    fun submit(msg: String) {
        queue.add(msg)
        if (!scheduled) {
            scheduled = true
            scope.launch {
                delay(15_000) // 15s batch window
                flush()
                scheduled = false
            }
        }
    }

    private fun flush() {
        val msgs = mutableListOf<String>()
        while (true) {
            val m = queue.poll() ?: break
            msgs.add(m)
        }
        if (msgs.isEmpty()) return

        val summary = if (msgs.size == 1) msgs.first() else "Summarized ${msgs.size} alerts"
        val detail = msgs.take(5).joinToString("\n")

        val notif = NotificationCompat.Builder(context, Channels.ALERTS)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle("GhostPrint")
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detail))
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(289578563, notif)
    }
}