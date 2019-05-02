package com.akay.fitnass.data.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.akay.fitnass.data.storage.converter.DateTimeConverter;
import com.akay.fitnass.data.storage.converter.ListConverter;
import com.akay.fitnass.data.storage.dao.ActiveRunsDao;
import com.akay.fitnass.data.storage.dao.RunsDao;
import com.akay.fitnass.data.storage.model.ActiveRuns;
import com.akay.fitnass.data.storage.model.Runs;

@TypeConverters({ListConverter.class, DateTimeConverter.class})
@Database(entities = {Runs.class, ActiveRuns.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RunsDao runsDao();

    public abstract ActiveRunsDao activeRunsDao();
}