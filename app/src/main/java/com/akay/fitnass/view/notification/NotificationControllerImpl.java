package com.akay.fitnass.view.notification;

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
import com.akay.fitnass.view.activities.TimerActivity;
import com.akay.fitnass.util.Logger;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationControllerImpl implements NotificationController {
    public static final String CHANNEL_ID = "com.akay.fitnass.ui.notification.channel_id_1000";
    private static final String CHANNEL_NAME = "Fitnass channel";
    private static final int PI_ACTIVITY_REQUEST_CODE = 3000;   // Request code of Pending Intent for launch MainActivity

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationControllerImpl(Context context) {
        mContext = context;
    }

    @Override
    public Notification getPersistentNotification() {
        return build(() -> getStartPauseStateBuilder(true).build());
    }

    @Override
    public void showStartPauseNotification(boolean showPauseText) {
        Logger.e("NotificationCntrl: showStartPauseNotification showPauseText:" + showPauseText);
        notify(FitService.FOREGROUND_SERVICE_ID, getStartPauseStateBuilder(showPauseText).build());
    }

    @Override
    public void notify(int id, Notification notification) {
        getNotificationManager().notify(id, notification);
    }

    private NotificationCompat.Builder getStartPauseStateBuilder(boolean showPauseText) {
        RemoteViews nView = new RemoteViews(mContext.getPackageName(), R.layout.notification_controls);

        Logger.e("NotificationCntrl: getStartPauseStateBuilder showPauseText:" + showPauseText);
        if (showPauseText) {
            Logger.e("Show pause text");
        } else {
            Logger.e("Show start text");
        }

        nView.setTextViewText(R.id.btn_start_pause, showPauseText ? "pause" : "start");

        Intent intentStart = new Intent(mContext, FitService.class);
        intentStart.setAction("start_pause");
        nView.setOnClickPendingIntent(R.id.btn_start_pause, PendingIntent.getService(mContext, 0, intentStart, PendingIntent.FLAG_UPDATE_CURRENT));

        return getBaseBuilder().setCustomContentView(nView);
    }

    private NotificationCompat.Builder getBaseBuilder() {
        Intent intent = TimerActivity.getIntent(mContext);
        PendingIntent pLaunchActivity = PendingIntent.getActivity(mContext, PI_ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews layout = new RemoteViews(mContext.getPackageName(), R.layout.notification_controls);

        Intent intentLap = new Intent(mContext, FitService.class);
        intentLap.setAction("lap");
        layout.setOnClickPendingIntent(R.id.btn_lap, PendingIntent.getService(mContext, 0, intentLap, PendingIntent.FLAG_UPDATE_CURRENT));

        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pLaunchActivity)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
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
