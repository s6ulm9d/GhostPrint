package com.ghostprint.ui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RawEventDao {
    @Insert
    suspend fun insert(event: RawEvent)

    @Query("SELECT * FROM raw_events ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<RawEvent>>

    @Query("DELETE FROM raw_events")
    suspend fun clearAll()
}