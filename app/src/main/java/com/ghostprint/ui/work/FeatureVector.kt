package com.ghostprint.ui.work

data class FeatureVector(
    val startTime: Long,
    val endTime: Long,
    val meanPressure: Double,
    val meanSize: Double,
    val eventCount: Int,
    val anomalyScore: Double,
    val meanAccelX: Double,
    val meanAccelY: Double,
    val meanAccelZ: Double,
    val stdAccelX: Double,
    val stdAccelY: Double,
    val stdAccelZ: Double,
    val meanGyroX: Double,
    val meanGyroY: Double,
    val meanGyroZ: Double,
    val stdGyroX: Double,
    val stdGyroY: Double,
    val stdGyroZ: Double
)