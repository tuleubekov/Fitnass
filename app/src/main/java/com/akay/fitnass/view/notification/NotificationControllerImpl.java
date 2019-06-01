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
import com.akay.fitnass.util.IntentBuilder;
import com.akay.fitnass.view.activities.TimerActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.akay.fitnass.service.FitService.NTFN_LAP_COMMAND;
import static com.akay.fitnass.service.FitService.NTFN_PAUSE_COMMAND;
import static com.akay.fitnass.service.FitService.NTFN_START_COMMAND;

public class NotificationControllerImpl implements NotificationController {
    public static final String CHANNEL_ID = "com.akay.fitnass.ui.notification.channel_id_1000";
    private static final String CHANNEL_NAME = "Fitnass channel";
    private static final int PI_ACTIVITY_REQUEST_CODE = 3000;   // Request code of Pending Intent for launch TimerActivity

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
        notify(FitService.FOREGROUND_SERVICE_ID, getStartPauseStateBuilder(showPauseText).build());
    }

    @Override
    public void notify(int id, Notification notification) {
        getNotificationManager().notify(id, notification);
    }

    private NotificationCompat.Builder getStartPauseStateBuilder(boolean showPauseText) {
        Intent startPauseIntent = getIntent(showPauseText ? NTFN_PAUSE_COMMAND : NTFN_START_COMMAND);
        Intent lapIntent = getIntent(NTFN_LAP_COMMAND);

        RemoteViews nView = new RemoteViews(mContext.getPackageName(), R.layout.notification_controls);
        nView.setInt(R.id.btn_ntfn_start_pause, "setBackgroundResource", showPauseText ? R.drawable.bg_ntfn_btn_paused : R.drawable.bg_ntfn_btn_started);
        nView.setInt(R.id.btn_ntfn__lap, "setBackgroundResource", showPauseText ? R.drawable.bg_ntfn_btn_lap_enabled : R.drawable.bg_ntfn_btn_lap_disabled);

        nView.setOnClickPendingIntent(R.id.btn_ntfn_start_pause, getPendingService(startPauseIntent));
        nView.setOnClickPendingIntent(R.id.btn_ntfn__lap, getPendingService(lapIntent));

        return getBaseBuilder().setCustomBigContentView(nView);
    }

    private NotificationCompat.Builder getBaseBuilder() {
        Intent intent = TimerActivity.getIntent(mContext);
        PendingIntent pLaunchActivity = getPendingActivity(intent);

        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pLaunchActivity)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setColor(ContextCompat.getColor(mContext, R.color.bg_notification))
                .setColorized(true)
                .setSound(null);
    }

    private PendingIntent getPendingService(final Intent intent) {
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingActivity(final Intent intent) {
        return PendingIntent.getActivity(mContext, PI_ACTIVITY_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private String getResString(int resId) {
        return mContext.getString(resId);
    }

    private Intent getIntent(String command) {
        return new IntentBuilder(mContext).toService().setCommand(command).build();
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

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
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
