package com.akay.fitnass.service;

import android.content.Context;

import com.akay.fitnass.App;

public class SourceProvider {

    public static WorkoutService provideWorkoutService(Context context) {
        return new WorkoutServiceImpl(App.getDb(context).workoutDao());
    }
}
