package com.ghostprint.ui.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ghostprint.ui.data.LogEvent
import com.ghostprint.ui.data.LogEventDao
import com.ghostprint.ui.service.LogStream
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData

/**
 * ViewModel for the Logs screen.
 * Provides both paged history from Room and live events from LogStream.
 */
class LogsViewModel(
    private val dao: LogEventDao
) : ViewModel() {

    // Paged history from Room
    val pagingFlow: Flow<PagingData<LogEvent>> =
        Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = false),
            pagingSourceFactory = { dao.pagingSource() }
        ).flow.cachedIn(viewModelScope)

    // Live stream of recent events
    val liveFlow = LogStream.events
}