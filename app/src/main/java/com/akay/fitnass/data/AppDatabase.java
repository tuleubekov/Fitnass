package com.akay.fitnass.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.akay.fitnass.data.converter.ListConverter;
import com.akay.fitnass.data.dao.WorkoutDao;
import com.akay.fitnass.data.model.Workout;

@TypeConverters(ListConverter.class)
@Database(entities = {Workout.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();
}
