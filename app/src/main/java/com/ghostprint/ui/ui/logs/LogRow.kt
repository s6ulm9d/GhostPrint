package com.ghostprint.ui.ui.logs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.view.accessibility.AccessibilityEvent
import com.ghostprint.ui.data.LogEvent

@Composable
fun LogRow(e: LogEvent) {
    Row {
        Text("${e.ts}", modifier = Modifier.width(160.dp))
        Text("${typeName(e.type)} ")
        Text("pkg=${e.pkg} cls=${e.cls} text=${e.text}")
    }
}

private fun typeName(type: Int): String = when (type) {
    AccessibilityEvent.TYPE_VIEW_CLICKED -> "view_clicked"
    AccessibilityEvent.TYPE_VIEW_FOCUSED -> "view_focused"
    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "text_changed"
    AccessibilityEvent.TYPE_VIEW_SCROLLED -> "view_scroll"
    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "window_state"
    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "window_content"
    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "notification_state"
    else -> "event_$type"
}