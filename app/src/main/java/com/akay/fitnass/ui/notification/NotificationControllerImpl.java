package com.akay.fitnass.ui.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.akay.fitnass.R;
import com.akay.fitnass.service.FitService;
import com.akay.fitnass.ui.main.MainActivity;
import com.akay.fitnass.ui.workoutadd.WorkoutAddActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationControllerImpl implements NotificationController {
    public static final String CHANNEL_ID = "com.akay.fitnass.ui.notification.channel_id_1000";
    private static final String CHANNEL_NAME = "Fitnass channel";
    private static final int PI_ACTIVITY_REQUEST_CODE = 3000;   // Request code of Pending Intent for launch MainActivity

    //TODO: Add Dagger later
    private static NotificationControllerImpl mInstance;
    private Context mContext;
    private NotificationManager mNotificationManager;

    private NotificationControllerImpl(Context context) {
        mContext = context;
    }

    public static NotificationController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotificationControllerImpl(context);
        }
        return mInstance;
    }

    @Override
    public Notification getPersistentNotification() {
        return build(() -> getBaseBuilder().build());
    }

    @Override
    public void notify(int id, Notification notification) {
        getNotificationManager().notify(id, notification);
    }

    private NotificationCompat.Builder getBaseBuilder() {
        Intent intent = WorkoutAddActivity.getIntent(mContext);
        PendingIntent pLaunchActivity = PendingIntent.getActivity(mContext, PI_ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews layout = new RemoteViews(mContext.getPackageName(), R.layout.notification_controls);

        Intent btnIntent = new Intent(mContext, FitService.class);
        btnIntent.setAction("log");
        layout.setOnClickPendingIntent(R.id.btn_log, PendingIntent.getService(mContext, 0, btnIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pLaunchActivity)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(layout)
                .setColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                .setSound(null);
    }

    private Notification build(NotificationBuildOperation o) {
        if (isAfterO()) {
            createChannel();
        }
        return o.runBuild();
    }

    private boolean isAfterO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setSound(null, att);

        getNotificationManager().createNotificationChannel(channel);
    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    @FunctionalInterface
    private interface NotificationBuildOperation {
        Notification runBuild();
    }

}
