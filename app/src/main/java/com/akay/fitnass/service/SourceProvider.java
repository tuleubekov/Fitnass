package com.akay.fitnass.service;

import android.content.Context;

import com.akay.fitnass.App;
import com.akay.fitnass.data.model.Workout;
import com.akay.fitnass.scheduler.SingleThreadScheduler;

public class SourceProvider {

    public static WorkoutService provideWorkoutService(Context context) {
        return WorkoutServiceImpl.getInstance(App.getDb(context).workoutDao(), SingleThreadScheduler.getInstance());
    }
}
