package com.ghostprint.ui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InputLogDao {
    @Insert
    suspend fun insert(log: InputLog)

    @Query("SELECT * FROM input_logs ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<InputLog>>

    @Query("DELETE FROM input_logs")
    suspend fun clearAll()
}