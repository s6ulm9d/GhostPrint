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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GhostPrintAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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

        // Configure service info explicitly to capture all relevant events
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 50
        }
        serviceInfo = info

        Channels.ensure(this)
        Log.i(TAG, "AccessibilityService connected (full event mask, interactive windows enabled)")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!::settings.isInitialized) settings = SettingsStore(this)
        if (!settings.isConsentEnabled() || settings.isLoggingPaused()) return

        val pkg = event.packageName?.toString() ?: return
        val cls = event.className?.toString() ?: "unknown"

        // Ignore dynamic UIs (e.g., popups, overlays, and modals)
        if (cls.contains("Popup") || cls.contains("Dialog") || cls.contains("Toast")) {
            Log.d(TAG, "Ignoring dynamic UI: $cls")
            return
        }

        // Filter out irrelevant apps (e.g., system apps or unwanted background apps)
        if (pkg in policy.packagesBlock) return
        if (policy.suppressDynamicRegions && cls in policy.classesBlock) return

        // Log event if it’s a valid interaction
        val ts = System.currentTimeMillis()
        val text = event.text?.joinToString() ?: ""

        val logEvent = LogEvent(
            ts = ts,
            pkg = pkg,
            cls = cls,
            type = event.eventType,
            text = text
        )

        serviceScope.launch { LogPipeline.enqueue(logEvent) }

        // Specific handling for home screen / app drawer (launcher events)
        if (pkg.contains("launcher") || pkg.contains("home")) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    Log.d(TAG, "HOME_SCREEN/DRAWER window state changed")
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    Log.d(TAG, "HOME_SCREEN/DRAWER content changed")
                }
            }
        }

        // Specific event types can be added here (e.g., if you want to track specific UI actions)

        // Anomaly detection
        if (detector.onEventTimestamp(ts)) {
            alertBatcher.submit("Interaction anomaly: ${event.eventType} in $pkg")
        }

        Log.v(TAG, "[$ts] pkg=$pkg cls=$cls type=${event.eventType} text=$text")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        if (!::settings.isInitialized) settings = SettingsStore(this)
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

        serviceScope.launch { LogPipeline.enqueue(logEvent) }

        if (detector.onKeyTimestamp(ts, isDown)) {
            alertBatcher.submit("Key timing anomaly: $msg")
        }

        Log.d(TAG, "[$ts] $type $msg")
        return false
    }

    override fun onInterrupt() {
        Log.w(TAG, "AccessibilityService interrupted")
    }

    companion object {
        private const val TAG = "GhostPrintService"
    }
}
