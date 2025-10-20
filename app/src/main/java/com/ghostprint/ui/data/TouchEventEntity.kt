package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "touch_events")
data class TouchEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val x: Float,
    val y: Float
)