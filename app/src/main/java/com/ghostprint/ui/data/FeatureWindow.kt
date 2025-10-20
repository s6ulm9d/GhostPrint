package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "feature_windows",
    indices = [Index(value = ["startTime", "endTime"], unique = false)]
)
data class FeatureWindow(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val startTime: Long,
    val endTime: Long,
    val meanAccelX: Float,
    val meanAccelY: Float,
    val meanAccelZ: Float,
    val stdAccelX: Float,
    val stdAccelY: Float,
    val stdAccelZ: Float,
    val meanGyroX: Float,
    val meanGyroY: Float,
    val meanGyroZ: Float,
    val stdGyroX: Float,
    val stdGyroY: Float,
    val stdGyroZ: Float,
    val avgTouchVelocity: Float? = null,
    val gestureDistance: Float? = null,
    val tapCount: Int? = null
)