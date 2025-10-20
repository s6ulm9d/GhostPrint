package com.ghostprint.ui.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.ghostprint.ui.data.AppDatabase
import com.ghostprint.ui.data.InputLog
import com.ghostprint.ui.domain.AnomalyDetector
import com.ghostprint.ui.domain.SettingsStore
import com.ghostprint.ui.notification.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GhostPrintAccessibilityService : AccessibilityService() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase
    private lateinit var detector: AnomalyDetector
    private lateinit var settings: SettingsStore

    override fun onServiceConnected() {
        super.onServiceConnected()
        db = AppDatabase.getInstance(this)
        detector = AnomalyDetector(this)
        settings = SettingsStore(this)

        serviceInfo = serviceInfo.apply {
            flags = flags or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }

        NotificationUtils.ensureChannels(this)
        Log.d("GhostPrintService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val consent = settings.isConsentEnabled()
        val paused = settings.isLoggingPaused()
        if (!consent || paused) return

        val ts = System.currentTimeMillis()
        val typeName = when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "view_click"
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "view_scroll"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "text_change"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "window_content"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "window_state"
            else -> "event_${event.eventType}"
        }
        val pkg = event.packageName?.toString() ?: "unknown"
        val cls = event.className?.toString() ?: "unknown"
        val text = event.text?.joinToString() ?: ""
        val msg = "pkg=$pkg cls=$cls text=$text"

        val isAnomaly = detector.onEventTimestamp(ts)

        ioScope.launch {
            db.inputLogDao().insert(InputLog(timestamp = ts, type = typeName, message = msg))
        }

        if (isAnomaly) {
            NotificationUtils.showAnomaly(this, "Interaction anomaly detected", "$typeName in $pkg")
        }

        Log.d("GhostPrintService", "[$ts] $typeName $msg anomaly=$isAnomaly")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false

        val consent = settings.isConsentEnabled()
        val paused = settings.isLoggingPaused()
        if (!consent || paused) return false

        val ts = System.currentTimeMillis()
        val isDown = event.action == KeyEvent.ACTION_DOWN
        val type = if (isDown) "key_down" else "key_up"
        val msg = "code=${event.keyCode} unicode=${event.unicodeChar}"

        val isAnomaly = detector.onKeyTimestamp(ts, isDown)

        ioScope.launch {
            db.inputLogDao().insert(InputLog(timestamp = ts, type = type, message = msg))
        }

        if (isAnomaly) {
            NotificationUtils.showAnomaly(this, "Key timing anomaly", msg)
        }

        Log.d("GhostPrintService", "[$ts] $type $msg anomaly=$isAnomaly")
        return false
    }

    override fun onInterrupt() {
        Log.d("GhostPrintService", "Service interrupted")
    }
}