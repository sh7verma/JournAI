package com.shverma.journai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shverma.journai.data.local.converter.DateTimeConverter
import com.shverma.journai.data.local.converter.StringListConverter
import com.shverma.journai.data.local.dao.JournalDao
import com.shverma.journai.data.local.entity.JournalEntity

@Database(
    entities = [JournalEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao

    companion object {
        private const val DATABASE_NAME = "journai_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}