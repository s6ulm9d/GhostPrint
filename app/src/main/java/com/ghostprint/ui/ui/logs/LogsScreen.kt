package com.ghostprint.ui.ui.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ghostprint.ui.data.LogEvent
import kotlinx.coroutines.flow.collect

/**
 * Composable screen that displays both live and historical logs.
 */
@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val lazyPagingItems: LazyPagingItems<LogEvent> =
        viewModel.pagingFlow.collectAsLazyPagingItems()

    // In-memory buffer for live events (capped at 300)
    val liveEvents = remember { mutableStateListOf<LogEvent>() }

    LaunchedEffect(Unit) {
        viewModel.liveFlow.collect { e: LogEvent ->
            liveEvents.add(0, e)
            if (liveEvents.size > 300) {
                liveEvents.removeLast()
            }
        }
    }

    Column {
        Text(
            text = "GhostPrint Logs",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn {
            // Live (non-paged) logs
            items(liveEvents) { e ->
                LogRow(e)
            }

            // Historical paged logs
            items(lazyPagingItems.itemCount) { index ->
                lazyPagingItems[index]?.let { e ->
                    LogRow(e)
                }
            }
        }
    }
}