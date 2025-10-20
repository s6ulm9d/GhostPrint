package com.ghostprint.ui.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FeatureWindowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(window: FeatureWindow): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(windows: List<FeatureWindow>): List<Long>

    @Query("SELECT * FROM feature_windows ORDER BY endTime DESC LIMIT :limit")
    suspend fun fetchLatest(limit: Int = 10): List<FeatureWindow>

    @Query("SELECT * FROM feature_windows WHERE startTime >= :start AND endTime <= :end ORDER BY endTime ASC")
    suspend fun fetchByTimeRange(start: Long, end: Long): List<FeatureWindow>
}