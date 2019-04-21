package com.akay.fitnass.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.akay.fitnass.services.ActiveWorkoutService;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.ui.notification.NotificationController;
import com.akay.fitnass.util.Logger;

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
        if ("log".equals(intent.getAction())) {
            Logger.e("Log action tapped");
        } else {
            Logger.e("action unknown");
        }
        return START_STICKY;
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
