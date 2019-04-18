package com.akay.fitnass.data.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.akay.fitnass.data.storage.converter.ListConverter;
import com.akay.fitnass.data.storage.dao.ActiveWorkoutDao;
import com.akay.fitnass.data.storage.dao.WorkoutDao;
import com.akay.fitnass.data.storage.model.ActiveWorkout;
import com.akay.fitnass.data.storage.model.Workout;

@TypeConverters(ListConverter.class)
@Database(entities = {Workout.class, ActiveWorkout.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();

    public abstract ActiveWorkoutDao activeWorkoutDao();
}
