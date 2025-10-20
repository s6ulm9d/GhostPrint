package com.ghostprint.ui.ui

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.ExperimentalComposeUiApi

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TouchLoggerComposable(onTouch: (x: Float, y: Float) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        onTouch(motionEvent.x, motionEvent.y)
                    }
                }
                true
            }
    )
}