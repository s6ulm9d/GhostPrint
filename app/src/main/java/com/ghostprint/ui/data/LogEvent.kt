package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_events")
data class LogEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ts: Long,
    val pkg: String,
    val cls: String,
    val type: Int,
    val text: String,
    val extras: String? = null
)