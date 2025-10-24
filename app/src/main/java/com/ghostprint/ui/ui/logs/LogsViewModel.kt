package com.ghostprint.ui.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghostprint.ui.data.LogEvent
import com.ghostprint.ui.data.LogEventDao
import com.ghostprint.ui.service.LogStream
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel for the Logs screen.
 * Exposes both paged history from Room and live events from LogStream.
 */
class LogsViewModel(
    private val dao: LogEventDao
) : ViewModel() {

    /**
     * Historical logs from Room, paged for efficiency.
     */
    val pagingFlow: Flow<PagingData<LogEvent>> =
        Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = { dao.pagingSource() }
        ).flow.cachedIn(viewModelScope)

    /**
     * Live stream of recent events from LogStream.
     */
    val liveFlow: Flow<LogEvent> = LogStream.events
}