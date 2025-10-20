package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "raw_events")
data class RawEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val eventType: String,
    val accelX: Float? = null,
    val accelY: Float? = null,
    val accelZ: Float? = null,
    val gyroX: Float? = null,
    val gyroY: Float? = null,
    val gyroZ: Float? = null
)