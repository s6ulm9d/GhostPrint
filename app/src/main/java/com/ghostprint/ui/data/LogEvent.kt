package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a captured accessibility log event.
 */
@Entity(tableName = "log_events")
data class LogEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ts: Long,          // timestamp (epoch millis)
    val pkg: String,       // package name
    val cls: String,       // class name
    val type: Int,         // event type
    val text: String,      // event text
    val extras: String? = null // optional serialized extras
)