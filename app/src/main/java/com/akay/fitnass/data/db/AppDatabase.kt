package com.akay.fitnass.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

import com.akay.fitnass.data.db.converter.DateTimeConverter
import com.akay.fitnass.data.db.converter.ListConverter
import com.akay.fitnass.data.db.dao.ActiveRunsDao
import com.akay.fitnass.data.db.dao.RunsDao
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Runs

@TypeConverters(ListConverter::class, DateTimeConverter::class)
@Database(entities = [Runs::class, ActiveRuns::class], version = 1, exportSchema = false)
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