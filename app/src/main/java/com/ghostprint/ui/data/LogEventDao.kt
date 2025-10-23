package com.ghostprint.ui.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LogEventDao {

    /**
     * Insert a batch of log events.
     * Uses IGNORE so duplicates (same primary key) are skipped.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<LogEvent>)

    /**
     * PagingSource for historical logs.
     * Ordered by id descending so newest events appear first.
     */
    @Query("SELECT * FROM log_events ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, LogEvent>

    /**
     * Delete old events before a given timestamp.
     */
    @Query("DELETE FROM log_events WHERE ts < :beforeMillis")
    suspend fun prune(beforeMillis: Long)
}