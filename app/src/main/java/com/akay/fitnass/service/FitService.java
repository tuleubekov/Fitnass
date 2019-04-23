package com.akay.fitnass.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.akay.fitnass.data.storage.model.ActiveWorkout;
import com.akay.fitnass.data.storage.model.TimerParams;
import com.akay.fitnass.services.ActiveWorkoutService;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.ui.notification.NotificationController;
import com.akay.fitnass.util.Logger;

import org.joda.time.DateTime;

public class FitService extends Service {
    public static final int FOREGROUND_SERVICE_ID = 1000;

    private ActiveWorkoutService mActiveWorkoutService;
    private NotificationController mNotificationController;

    @Override
    public void onCreate() {
        super.onCreate();
        mActiveWorkoutService = SourceProvider.provideActiveWorkoutService(this);
        mNotificationController = SourceProvider.provideNotificationController(this);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationController.getPersistentNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getAction();
        if (TextUtils.isEmpty(command)) {
            Logger.e("Service: action command null or empty");
            return START_STICKY;
        }

        switch (intent.getAction()) {
            case "start_pause":
                Logger.e("Service: action start_pause tapped");
                startPauseAction();
                break;
            case "lap":
                Logger.e("Service: action lap tapped");
                lap();
                break;
            default: Logger.e("Service: action unknown");
        }
        return START_STICKY;
    }

    private void startPauseAction() {
        ActiveWorkout activeWorkout = mActiveWorkoutService.getActiveSession();
        boolean isPaused = activeWorkout.isPaused();
        Logger.e("Service: startPauseAction clicked. ActiveWorkout state is paused: " + isPaused);

        ActiveWorkout activeWorkout1 = buildActiveWorkout(!isPaused);
        mActiveWorkoutService.upsert(activeWorkout1);
        Logger.e("Service: ActiveWorkout saved new value. isPaused: " + activeWorkout1.isPaused());

        mNotificationController.showStartPauseNotification(!activeWorkout1.isPaused());
    }

    private void lap() {
        Logger.e("Click lap button");
    }

    private ActiveWorkout buildActiveWorkout(boolean isPaused) {
        ActiveWorkout activeWorkout = new ActiveWorkout();
        activeWorkout.setId(ActiveWorkout.ID);
        activeWorkout.setPaused(isPaused);
        activeWorkout.setDate(DateTime.now().getMillis());
        return activeWorkout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
