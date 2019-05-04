package com.akay.fitnass.di;

import android.content.Context;

import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.RunsRepositoryImpl;
import com.akay.fitnass.data.db.AppDatabase;
import com.akay.fitnass.data.db.dao.ActiveRunsDao;
import com.akay.fitnass.data.db.dao.RunsDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class StorageModule {

    @Provides @Singleton
    public AppDatabase provideRoomDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }

    @Provides @Singleton
    public ActiveRunsDao provideActiveRunsDao(AppDatabase appDatabase) {
        return appDatabase.activeRunsDao();
    }

    @Provides @Singleton
    public RunsDao provideRunsDao(AppDatabase appDatabase) {
        return appDatabase.runsDao();
    }

    @Provides @Singleton
    public RunsRepository provideRepository(ActiveRunsDao activeRunsDao, RunsDao runsDao) {
        return new RunsRepositoryImpl(activeRunsDao, runsDao);
    }
}
