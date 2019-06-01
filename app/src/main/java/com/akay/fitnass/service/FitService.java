package com.akay.fitnass.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Lap;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.view.notification.NotificationController;
import com.akay.fitnass.util.Logger;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.akay.fitnass.util.AppUtils.vibrate;
import static com.akay.fitnass.util.DateTimes.fromMs;
import static com.akay.fitnass.util.DateTimes.nowMillis;
import static com.akay.fitnass.util.DateTimes.toMs;

public class FitService extends Service {
    public static final int FOREGROUND_SERVICE_ID = 1000;
    public static final String START_COMMAND = "COMMAND_START";
    public static final String PAUSE_COMMAND = "COMMAND_PAUSE";
    public static final String SAVE_COMMAND = "COMMAND_SAVE";
    public static final String LAP_COMMAND = "COMMAND_LAP";
    public static final String RESET_COMMAND = "COMMAND_RESET";
    public static final String NTFN_START_COMMAND = "NOTIFICATION_START_COMMAND";
    public static final String NTFN_PAUSE_COMMAND = "NOTIFICATION_PAUSE_COMMAND";
    public static final String NTFN_LAP_COMMAND = "NOTIFICATION_LAP_COMMAND";
    public static final String INIT_STATE_COMMAND = "CHECK_STATE_COMMAND";

    @Inject RunsRepository mRepository;
    @Inject NotificationController mNotificationController;

    private ActiveRuns mActiveRuns;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationController.getPersistentNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getAction();
        if (command == null) {
            mActiveRuns = get();
            if (mActiveRuns == null) {
                return START_NOT_STICKY;
            }
            command = INIT_STATE_COMMAND;
        }

        long ms = nowMillis();
        switch (command) {
            case INIT_STATE_COMMAND: init(); break;
            case START_COMMAND: start(ms); break;
            case PAUSE_COMMAND: pause(ms); break;
            case SAVE_COMMAND: save(); break;
            case LAP_COMMAND: lap(ms); break;
            case RESET_COMMAND: reset(); break;
            case NTFN_START_COMMAND: start(ms); break;
            case NTFN_PAUSE_COMMAND: pause(ms); break;
            case NTFN_LAP_COMMAND: {
                vibrate(this);
                lap(ms);
            } break;
            default: Logger.e("Unknown command: " + command);
        }
        return START_STICKY;
    }

    private void init() {
        mActiveRuns = get();
        if (mActiveRuns.isPaused()) {
            showStartNotification();
        } else {
            showPauseNotification();
        }
    }

    private void start(long ms) {
        mActiveRuns = get();
        if (mActiveRuns == null) {
            mActiveRuns = new ActiveRuns();
            mActiveRuns.setDateTime(ZonedDateTime.now());
            mActiveRuns.setLaps(new ArrayList<>());
        }

        long msStart = ms + toMs(mActiveRuns.getTws());
        mActiveRuns.setPaused(false);
        mActiveRuns.setStart(fromMs(msStart));
        mRepository.upsertActiveRuns(mActiveRuns);
        showPauseNotification();
    }

    private void pause(long ms) {
        mActiveRuns = get();
        long msPause = toMs(mActiveRuns.getStart()) - ms;

        mActiveRuns.setPaused(true);
        mActiveRuns.setTws(fromMs(msPause));
        mRepository.upsertActiveRuns(mActiveRuns);
        showStartNotification();
    }

    private void save() {
        mActiveRuns = get();
        mRepository.saveRuns(mapFromActive(mActiveRuns));
        mRepository.deleteActiveRuns();
        stopSelf();
    }

    private void lap(long ms) {
        mActiveRuns = get();
        long msAction = ms - toMs(mActiveRuns.getStart());

        List<Lap> laps = mActiveRuns.getLaps();
        laps.add(getNewLap(msAction));
        mActiveRuns.setLaps(laps);
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void reset() {
        mActiveRuns = null;
        mRepository.deleteActiveRuns();
        stopSelf();
    }

    private void showStartNotification() {
        mNotificationController.showStartPauseNotification(false);
    }

    private void showPauseNotification() {
        mNotificationController.showStartPauseNotification(true);
    }

    private ActiveRuns get() {
        return mRepository.getActiveRuns();
    }

    private Runs mapFromActive(final ActiveRuns activeRuns) {
        Runs runs = new Runs();
        runs.setDateTime(activeRuns.getDateTime());
        runs.setLaps(activeRuns.getLaps());
        return runs;
    }

    private Lap getNewLap(long ms) {
        Lap lap = new Lap();
        lap.setTime(fromMs(ms));
        return lap;
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
