package com.akay.fitnass.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.akay.fitnass.data.dao.WorkoutDao;
import com.akay.fitnass.data.model.Workout;

@Database(entities = {Workout.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();
}
