package com.ghostprint.ui.ui.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // for List<T>
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items // for LazyPagingItems<T>
import com.ghostprint.ui.data.LogEvent

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val lazyPagingItems: LazyPagingItems<LogEvent> =
        viewModel.pagingFlow.collectAsLazyPagingItems()
    val liveEvents = remember { mutableStateListOf<LogEvent>() }

    LaunchedEffect(Unit) {
        viewModel.liveFlow.collect { e: LogEvent ->
            liveEvents.add(0, e)
            if (liveEvents.size > 300) liveEvents.removeLast()
        }
    }

    Column {
        Text("GhostPrint Logs", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            // Live events first (List<T>)
            items(liveEvents) { e ->
                LogRow(e)
            }

            // Historical paged events (LazyPagingItems<T>)
            items(lazyPagingItems) { e ->
                if (e != null) LogRow(e)
            }
        }
    }
}