package com.akay.fitnass.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.akay.fitnass.App
import com.akay.fitnass.data.RunsRepository
import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Lap
import com.akay.fitnass.data.model.Runs
import com.akay.fitnass.util.Logger
import com.akay.fitnass.view.notification.NotificationController

import org.threeten.bp.ZonedDateTime

import java.util.ArrayList

import javax.inject.Inject

import com.akay.fitnass.util.AppUtils.vibrate
import com.akay.fitnass.util.DateTimes.fromMs
import com.akay.fitnass.util.DateTimes.nowMillis
import com.akay.fitnass.util.DateTimes.toMs

class FitService : Service() {

    @Inject lateinit var mRepository: RunsRepository
    @Inject lateinit var mNotificationController: NotificationController

    private var mActiveRuns: ActiveRuns? = null
    private var mLastClickTime = 0L

    private val isNotSafeTemp: Boolean
        get() {
            if (nowMillis() - mLastClickTime < SAFE_TEMP_INTERVAL) {
                return true
            }
            mLastClickTime = nowMillis()
            return false
        }

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
        startForeground(FOREGROUND_SERVICE_ID, mNotificationController.getPersistentNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        val command = action ?: INIT_STATE_COMMAND
        mActiveRuns = get()

        if (mActiveRuns == null && command != START_COMMAND) {
            stopSelf()
            return START_NOT_STICKY
        }

        val ms = nowMillis()
        when (command) {
            INIT_STATE_COMMAND -> init()
            START_COMMAND -> start(ms)
            PAUSE_COMMAND -> pause(ms)
            SAVE_COMMAND -> save()
            LAP_COMMAND -> lap(ms)
            RESET_COMMAND -> reset()
            NTFN_START_COMMAND -> start(ms)
            NTFN_PAUSE_COMMAND -> pause(ms)
            NTFN_LAP_COMMAND -> ntfnLap(ms)
            else -> Logger.e("Unknown command: $command")
        }
        return START_STICKY
    }

    private fun init() {
        if (mActiveRuns!!.isPaused!!) {
            showStartNotification()
        } else {
            showPauseNotification()
        }
    }

    private fun start(ms: Long) {
        if (mActiveRuns == null) {
            mActiveRuns = ActiveRuns(dateTime = ZonedDateTime.now(), laps = ArrayList())
        }

        val msStart = ms + toMs(mActiveRuns!!.tws)
        mActiveRuns!!.isPaused = false
        mActiveRuns!!.start = fromMs(msStart)
        mRepository.upsertActiveRuns(mActiveRuns!!)
        showPauseNotification()
    }

    private fun pause(ms: Long) {
        val msPause = toMs(mActiveRuns!!.start) - ms

        mActiveRuns!!.isPaused = true
        mActiveRuns!!.tws = fromMs(msPause)
        mRepository.upsertActiveRuns(mActiveRuns!!)
        showStartNotification()
    }

    private fun save() {
        mRepository.saveRuns(mapFromActive(mActiveRuns!!))
        mRepository.deleteActiveRuns()
        stopSelf()
    }

    private fun lap(ms: Long) {
        val msAction = ms - toMs(mActiveRuns!!.start)

        val laps = mActiveRuns!!.laps
        laps!!.add(getNewLap(msAction))
        mActiveRuns!!.laps = laps
        mRepository.upsertActiveRuns(mActiveRuns!!)
    }

    private fun reset() {
//        mActiveRuns = null
        mRepository.deleteActiveRuns()
        stopSelf()
    }

    private fun ntfnLap(ms: Long) {
        if (isNotSafeTemp) {
            return
        }
        if (!mActiveRuns!!.isPaused!!) {
            vibrate(this)
            lap(ms)
        }
    }

    private fun showStartNotification() = mNotificationController.showStartPauseNotification(false)

    private fun showPauseNotification() = mNotificationController.showStartPauseNotification(true)

    private fun get(): ActiveRuns = mRepository.getActiveRuns()

    private fun mapFromActive(activeRuns: ActiveRuns): Runs = Runs(laps = activeRuns.laps!!, dateTime = activeRuns.dateTime)

    private fun getNewLap(ms: Long): Lap = Lap(time = fromMs(ms))

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        const val FOREGROUND_SERVICE_ID = 1000
        const val SAFE_TEMP_INTERVAL = 1000L
        const val START_COMMAND = "COMMAND_START"
        const val PAUSE_COMMAND = "COMMAND_PAUSE"
        const val SAVE_COMMAND = "COMMAND_SAVE"
        const val LAP_COMMAND = "COMMAND_LAP"
        const val RESET_COMMAND = "COMMAND_RESET"
        const val NTFN_START_COMMAND = "NOTIFICATION_START_COMMAND"
        const val NTFN_PAUSE_COMMAND = "NOTIFICATION_PAUSE_COMMAND"
        const val NTFN_LAP_COMMAND = "NOTIFICATION_LAP_COMMAND"
        const val INIT_STATE_COMMAND = "CHECK_STATE_COMMAND"
    }
}
