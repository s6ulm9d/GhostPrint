package com.ghostprint.ui.controller

import android.content.Context
import android.content.Intent
import com.ghostprint.ui.service.LoggerForegroundService

object ServiceController {
    fun startLogger(context: Context) {
        val intent = Intent(context, LoggerForegroundService::class.java)
        context.startForegroundService(intent)
    }

    fun stopLogger(context: Context) {
        val intent = Intent(context, LoggerForegroundService::class.java)
        context.stopService(intent)
    }
}