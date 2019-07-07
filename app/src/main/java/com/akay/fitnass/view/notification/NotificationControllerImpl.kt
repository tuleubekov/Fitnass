package com.akay.fitnass.view.notification

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews

import com.akay.fitnass.R
import com.akay.fitnass.util.IntentBuilder
import com.akay.fitnass.view.activities.MainActivity
import com.akay.fitnass.view.activities.TimerActivity

import android.content.Context.NOTIFICATION_SERVICE
import com.akay.fitnass.service.FitService.Companion.FOREGROUND_SERVICE_ID
import com.akay.fitnass.service.FitService.Companion.NTFN_LAP_COMMAND
import com.akay.fitnass.service.FitService.Companion.NTFN_PAUSE_COMMAND
import com.akay.fitnass.service.FitService.Companion.NTFN_START_COMMAND
import com.akay.fitnass.util.AppUtils.isAfterO

class NotificationControllerImpl(private val mContext: Context) : NotificationController {
    private var mNotificationManager: NotificationManager? = null

    private fun getBaseBuilder(): NotificationCompat.Builder {
        val intents = arrayOf(MainActivity.getIntent(mContext), TimerActivity.getIntent(mContext))
        val pLaunchActivity = getPendingActivity(intents)

        return NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pLaunchActivity)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(false)
                .setSound(null)
    }

    private val notificationManager: NotificationManager
        get() {
            return mNotificationManager ?: mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

    override fun getPersistentNotification(): Notification {
        return setup { getStartPauseStateBuilder(true).build() }
    }

    override fun showStartPauseNotification(showPause: Boolean) {
        notify(FOREGROUND_SERVICE_ID, getStartPauseStateBuilder(showPause).build())
    }

    override fun notify(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    private fun getStartPauseStateBuilder(showPause: Boolean): NotificationCompat.Builder {
        val startPauseIntent = getIntent(if (showPause) NTFN_PAUSE_COMMAND else NTFN_START_COMMAND)
        val lapIntent = getIntent(NTFN_LAP_COMMAND)

        val nView = RemoteViews(mContext.packageName, R.layout.notification_controls)
        nView.setInt(R.id.btn_ntfn_start_pause, "setBackgroundResource", if (showPause) R.drawable.bg_ntfn_btn_paused else R.drawable.bg_ntfn_btn_started)
        nView.setInt(R.id.btn_ntfn__lap, "setBackgroundResource", if (showPause) R.drawable.bg_ntfn_btn_lap_enabled else R.drawable.bg_ntfn_btn_lap_disabled)
        nView.setViewVisibility(R.id.view_cover, if (showPause) View.GONE else View.VISIBLE)

        nView.setOnClickPendingIntent(R.id.btn_ntfn_start_pause, getPendingService(startPauseIntent))
        nView.setOnClickPendingIntent(R.id.btn_ntfn__lap, getPendingService(lapIntent))

        return getBaseBuilder().setCustomContentView(nView)
    }

    private fun getPendingService(intent: Intent): PendingIntent {
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingActivity(intents: Array<Intent>): PendingIntent {
        return PendingIntent.getActivities(mContext, PI_ACTIVITY_REQUEST_CODE, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getResString(resId: Int): String {
        return mContext.getString(resId)
    }

    private fun getIntent(command: String): Intent {
        return IntentBuilder(mContext).toService().setCommand(command).build()
    }

    private fun setup(build: () -> Notification): Notification {
        if (isAfterO) {
            createChannel()
        }
        return build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setSound(null, att)

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "com.akay.fitnass.ui.notification.channel_id_1000"
        private const val CHANNEL_NAME = "Fitnass channel"
        private const val PI_ACTIVITY_REQUEST_CODE = 3000   // Request code of Pending Intent for launch TimerActivity
    }

}
