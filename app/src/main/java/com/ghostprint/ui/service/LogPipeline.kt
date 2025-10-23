package com.ghostprint.ui.service

import com.ghostprint.ui.data.LogEvent
import com.ghostprint.ui.data.LogEventDao
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicInteger

object LogPipeline {
    private const val RING_SIZE = 2048
    private val ring = ArrayDeque<LogEvent>(RING_SIZE)
    private val ringSize = AtomicInteger(0)

    private val dbChannel = Channel<LogEvent>(capacity = 8192)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start(daoProvider: () -> LogEventDao) {
        scope.launch {
            val dao = daoProvider()
            val batch = mutableListOf<LogEvent>()
            while (isActive) {
                val first = dbChannel.receive()
                batch.clear()
                batch.add(first)
                withTimeoutOrNull(100) {
                    repeat(499) {
                        val item = dbChannel.tryReceive().getOrNull() ?: return@repeat
                        batch.add(item)
                    }
                }
                dao.insertAll(batch)
            }
        }
    }

    fun stop() {
        scope.cancel()
        dbChannel.close()
    }

    fun enqueue(event: LogEvent) {
        synchronized(ring) {
            if (ringSize.get() >= RING_SIZE) {
                ring.removeFirst()
                ringSize.decrementAndGet()
            }
            ring.addLast(event)
            ringSize.incrementAndGet()
        }
        LogStream.dispatch(event)
        dbChannel.trySend(event)
    }

    fun snapshot(): List<LogEvent> {
        synchronized(ring) { return ring.toList().reversed() }
    }
}