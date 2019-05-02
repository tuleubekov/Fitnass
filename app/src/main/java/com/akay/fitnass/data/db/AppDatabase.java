package com.akay.fitnass.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.akay.fitnass.data.db.converter.DateTimeConverter;
import com.akay.fitnass.data.db.converter.ListConverter;
import com.akay.fitnass.data.db.dao.ActiveRunsDao;
import com.akay.fitnass.data.db.dao.RunsDao;
import com.akay.fitnass.data.db.model.ActiveRuns;
import com.akay.fitnass.data.db.model.Runs;

@TypeConverters({ListConverter.class, DateTimeConverter.class})
@Database(entities = {Runs.class, ActiveRuns.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RunsDao runsDao();

    public abstract ActiveRunsDao activeRunsDao();
}