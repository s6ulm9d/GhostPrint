package com.ghostprint.ui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TouchEventDao {
    @Insert
    suspend fun insert(event: TouchEventEntity)

    @Query("SELECT * FROM touch_events ORDER BY timestamp DESC")
    suspend fun getAll(): List<TouchEventEntity>
}