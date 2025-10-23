package com.ghostprint.ui.policy

import android.view.accessibility.AccessibilityEvent

data class CapturePolicy(
    val packagesBlock: Set<String> = emptySet(),
    val classesBlock: Set<String> = emptySet(),
    val eventTypesAllow: Set<Int> = DEFAULT_EVENT_TYPES,
    val contentChangeTypesAllowMask: Int = DEFAULT_CONTENT_CHANGE_MASK,
    val suppressDynamicRegions: Boolean = true
)

private val DEFAULT_EVENT_TYPES = setOf(
    AccessibilityEvent.TYPE_VIEW_CLICKED,
    AccessibilityEvent.TYPE_VIEW_FOCUSED,
    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
    AccessibilityEvent.TYPE_VIEW_SCROLLED,
    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
)

private const val CC_CONTENT_DESCRIPTION =
    AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION
private const val CC_SUBTREE =
    AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE
private const val CC_TEXT =
    AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT

private const val DEFAULT_CONTENT_CHANGE_MASK =
    CC_SUBTREE or CC_TEXT or CC_CONTENT_DESCRIPTION

object DefaultPolicies {
    val BLOCK_PACKAGES = setOf(
        // System UI and overlays
        "com.android.systemui",
        // Media and dynamic content
        "com.spotify.music",
        "com.google.android.youtube",
        // Launchers are often very chatty
        "com.android.launcher",
        "com.google.android.apps.nexuslauncher"
    )

    val BLOCK_CLASSES = setOf(
        // Highly dynamic containers and overlays
        "android.widget.FrameLayout",
        "androidx.recyclerview.widget.RecyclerView",
        "android.inputmethodservice.SoftInputWindow"
    )
}