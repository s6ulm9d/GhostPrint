package com.ghostprint.ui.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.ghostprint.ui.data.LogEvent
import com.ghostprint.ui.domain.AnomalyDetector
import com.ghostprint.ui.domain.SettingsStore
import com.ghostprint.ui.notify.AlertBatcher
import com.ghostprint.ui.notify.Channels
import com.ghostprint.ui.policy.CapturePolicy
import com.ghostprint.ui.policy.DefaultPolicies
import com.ghostprint.ui.service.LogPipeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AccessibilityService that captures user interaction events
 * (with explicit consent) and pushes them into the LogPipeline.
 */
class GhostPrintAccessibilityService : AccessibilityService() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var detector: AnomalyDetector
    private lateinit var settings: SettingsStore
    private lateinit var alertBatcher: AlertBatcher

    private val policy = CapturePolicy(
        packagesBlock = DefaultPolicies.BLOCK_PACKAGES,
        classesBlock = DefaultPolicies.BLOCK_CLASSES
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        detector = AnomalyDetector(this)
        settings = SettingsStore(this)
        alertBatcher = AlertBatcher(this)

        serviceInfo = serviceInfo.apply {
            flags = flags or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }

        Channels.ensure(this)
        Log.d("GhostPrintService", "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!settings.isConsentEnabled() || settings.isLoggingPaused()) return

        val pkg = event.packageName?.toString() ?: return
        val cls = event.className?.toString() ?: ""

        // Apply capture policy
        if (pkg in policy.packagesBlock) return
        if (policy.suppressDynamicRegions && cls in policy.classesBlock) return

        val ts = System.currentTimeMillis()
        val text = event.text?.joinToString() ?: ""

        val logEvent = LogEvent(
            ts = ts,
            pkg = pkg,
            cls = cls,
            type = event.eventType,
            text = text
        )

        ioScope.launch { LogPipeline.enqueue(logEvent) }

        val isAnomaly = detector.onEventTimestamp(ts)
        if (isAnomaly) {
            alertBatcher.submit("Interaction anomaly: ${event.eventType} in $pkg")
        }

        Log.d("GhostPrintService", "[$ts] pkg=$pkg cls=$cls text=$text anomaly=$isAnomaly")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        if (!settings.isConsentEnabled() || settings.isLoggingPaused()) return false

        val ts = System.currentTimeMillis()
        val isDown = event.action == KeyEvent.ACTION_DOWN
        val type = if (isDown) "key_down" else "key_up"
        val msg = "code=${event.keyCode} unicode=${event.unicodeChar}"

        val logEvent = LogEvent(
            ts = ts,
            pkg = "key",
            cls = type,
            type = event.keyCode,
            text = msg
        )

        ioScope.launch { LogPipeline.enqueue(logEvent) }

        val isAnomaly = detector.onKeyTimestamp(ts, isDown)
        if (isAnomaly) {
            alertBatcher.submit("Key timing anomaly: $msg")
        }

        Log.d("GhostPrintService", "[$ts] $type $msg anomaly=$isAnomaly")
        return false
    }

    override fun onInterrupt() {
        Log.d("GhostPrintService", "AccessibilityService interrupted")
    }
}