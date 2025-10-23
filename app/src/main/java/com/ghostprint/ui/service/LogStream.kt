package com.ghostprint.ui.service

import com.ghostprint.ui.data.LogEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LogStream {
    private val _events = MutableSharedFlow<LogEvent>(replay = 0, extraBufferCapacity = 1024)
    val events = _events.asSharedFlow()

    fun dispatch(e: LogEvent) {
        _events.tryEmit(e)
    }
}