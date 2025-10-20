package com.ghostprint.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "input_logs")
data class InputLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: String,
    val message: String
)