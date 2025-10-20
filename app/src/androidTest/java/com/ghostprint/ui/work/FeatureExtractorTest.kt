package com.ghostprint.ui.work

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class FeatureExtractorTest {

    @Test
    fun testFeatureExtraction() {
        val events = listOf(
            TouchEvent(
                timestamp = 1L,
                pressure = 0.5f,
                size = 0.2f,
                accelX = 1f, accelY = 2f, accelZ = 3f,
                gyroX = 0.1f, gyroY = 0.2f, gyroZ = 0.3f
            ),
            TouchEvent(
                timestamp = 2L,
                pressure = 0.7f,
                size = 0.4f,
                accelX = 2f, accelY = 3f, accelZ = 4f,
                gyroX = 0.2f, gyroY = 0.3f, gyroZ = 0.4f
            )
        )

        val extractor = FeatureExtractor()
        val features = extractor.extractFeatures(events)

        assertEquals(1L, features.startTime)
        assertEquals(2L, features.endTime)
        assertEquals(2, features.eventCount)
        assertTrue(features.meanPressure > 0.0)
        assertTrue(features.meanSize > 0.0)
        assertEquals(1.5, features.meanAccelX, 0.001)
        assertEquals(2.5, features.meanAccelY, 0.001)
        assertEquals(3.5, features.meanAccelZ, 0.001)
    }
}