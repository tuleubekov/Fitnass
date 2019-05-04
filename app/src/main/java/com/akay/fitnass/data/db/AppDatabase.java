package com.akay.fitnass.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.akay.fitnass.data.db.converter.DateTimeConverter;
import com.akay.fitnass.data.db.converter.ListConverter;
import com.akay.fitnass.data.db.dao.ActiveRunsDao;
import com.akay.fitnass.data.db.dao.RunsDao;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Runs;

@TypeConverters({ListConverter.class, DateTimeConverter.class})
@Database(entities = {Runs.class, ActiveRuns.class}, version = AppDatabase.VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "FitnassDatabase";
    private static AppDatabase mInstance;
    static final int VERSION = 1;

    public abstract RunsDao runsDao();

    public abstract ActiveRunsDao activeRunsDao();

    public static AppDatabase getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return mInstance;
    }
}