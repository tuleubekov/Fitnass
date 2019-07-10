package com.akay.fitnass.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

import com.akay.fitnass.data.db.converter.DateTimeConverter
import com.akay.fitnass.data.db.converter.ListConverter
import com.akay.fitnass.data.db.dao.ActiveRunsDao
import com.akay.fitnass.data.db.dao.RunsDao
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Runs

@TypeConverters(ListConverter::class, DateTimeConverter::class)
@Database(entities = [Runs::class, ActiveRuns::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun runsDao(): RunsDao

    abstract fun activeRunsDao(): ActiveRunsDao

    companion object {
        private const val DB_NAME = "FitnassDatabase"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}