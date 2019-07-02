package com.akay.fitnass.di

import android.content.Context

import com.akay.fitnass.data.RunsRepository
import com.akay.fitnass.data.RunsRepositoryImpl
import com.akay.fitnass.data.db.AppDatabase
import com.akay.fitnass.data.db.dao.ActiveRunsDao
import com.akay.fitnass.data.db.dao.RunsDao

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
@Singleton
class StorageModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideActiveRunsDao(appDatabase: AppDatabase): ActiveRunsDao = appDatabase.activeRunsDao()

    @Provides
    @Singleton
    fun provideRunsDao(appDatabase: AppDatabase): RunsDao = appDatabase.runsDao()

    @Provides
    @Singleton
    fun provideRepository(activeRunsDao: ActiveRunsDao, runsDao: RunsDao): RunsRepository = RunsRepositoryImpl(activeRunsDao, runsDao)
}
