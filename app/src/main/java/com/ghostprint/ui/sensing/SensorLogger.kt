package com.ghostprint.ui.sensing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ghostprint.ui.data.AppDatabase
import com.ghostprint.ui.data.RawEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorLogger(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val db = AppDatabase.getInstance(context)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun start() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() = sensorManager.unregisterListener(this)

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val ts = System.currentTimeMillis()
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val raw = RawEvent(
                    timestamp = ts, eventType = "accel",
                    accelX = event.values[0], accelY = event.values[1], accelZ = event.values[2]
                )
                ioScope.launch { db.rawEventDao().insert(raw) }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val raw = RawEvent(
                    timestamp = ts, eventType = "gyro",
                    gyroX = event.values[0], gyroY = event.values[1], gyroZ = event.values[2]
                )
                ioScope.launch { db.rawEventDao().insert(raw) }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}