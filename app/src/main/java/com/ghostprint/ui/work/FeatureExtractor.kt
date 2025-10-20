package com.ghostprint.ui.work

import kotlin.math.sqrt

class FeatureExtractor {

    fun extractFeatures(events: List<TouchEvent>): FeatureVector {
        if (events.isEmpty()) {
            throw IllegalArgumentException("No events to extract features from")
        }

        val startTime = events.first().timestamp
        val endTime = events.last().timestamp
        val eventCount = events.size

        val meanPressure = events.mapNotNull { it.pressure?.toDouble() }.average()
        val meanSize = events.mapNotNull { it.size?.toDouble() }.average()

        val accelX = events.map { it.accelX }
        val accelY = events.map { it.accelY }
        val accelZ = events.map { it.accelZ }

        val gyroX = events.map { it.gyroX }
        val gyroY = events.map { it.gyroY }
        val gyroZ = events.map { it.gyroZ }

        return FeatureVector(
            startTime = startTime,
            endTime = endTime,
            meanPressure = meanPressure,
            meanSize = meanSize,
            eventCount = eventCount,
            anomalyScore = 0.0,
            meanAccelX = accelX.map { it.toDouble() }.average(),
            meanAccelY = accelY.map { it.toDouble() }.average(),
            meanAccelZ = accelZ.map { it.toDouble() }.average(),
            stdAccelX = accelX.stdDev(),
            stdAccelY = accelY.stdDev(),
            stdAccelZ = accelZ.stdDev(),
            meanGyroX = gyroX.map { it.toDouble() }.average(),
            meanGyroY = gyroY.map { it.toDouble() }.average(),
            meanGyroZ = gyroZ.map { it.toDouble() }.average(),
            stdGyroX = gyroX.stdDev(),
            stdGyroY = gyroY.stdDev(),
            stdGyroZ = gyroZ.stdDev()
        )
    }
}

data class TouchEvent(
    val timestamp: Long,
    val pressure: Float?,
    val size: Float?,
    val accelX: Float,
    val accelY: Float,
    val accelZ: Float,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float
)

fun List<Float>.stdDev(): Double {
    if (isEmpty()) return 0.0
    val mean = this.map { it.toDouble() }.average()
    val variance = this.map { (it - mean) * (it - mean) }.average()
    return sqrt(variance)
}