package com.ghostprint.ui.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Central Room database for GhostPrint.
 * Holds InputLog, RawEvent, and LogEvent tables with their DAOs.
 */
@Database(
    entities = [
        InputLog::class,
        RawEvent::class,
        LogEvent::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inputLogDao(): InputLogDao
    abstract fun rawEventDao(): RawEventDao
    abstract fun logEventDao(): LogEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton accessor for the database.
         * Uses applicationContext to avoid leaking an Activity.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ghostprint.db"
                )
                    // Safe for dev; replace with proper migrations in production.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}