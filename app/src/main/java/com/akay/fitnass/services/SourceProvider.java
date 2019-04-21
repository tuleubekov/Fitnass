package com.akay.fitnass.services;

import android.content.Context;

import com.akay.fitnass.App;
import com.akay.fitnass.scheduler.SingleThreadScheduler;
import com.akay.fitnass.ui.notification.NotificationController;
import com.akay.fitnass.ui.notification.NotificationControllerImpl;

public class SourceProvider {

    public static WorkoutService provideWorkoutService(Context context) {
        return WorkoutServiceImpl.getInstance(App.getDb(context).workoutDao(), SingleThreadScheduler.getInstance());
    }

    public static ActiveWorkoutService provideActiveWorkoutService(Context context) {
        return ActiveWorkoutServiceImpl.getInstance(App.getDb(context).activeWorkoutDao(), SingleThreadScheduler.getInstance());
    }

    public static NotificationController provideNotificationController(Context context) {
        return NotificationControllerImpl.getInstance(context);
    }
}
