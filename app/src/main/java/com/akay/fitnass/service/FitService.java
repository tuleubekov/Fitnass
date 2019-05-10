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
import com.akay.fitnass.util.DateTimeUtils;
import com.akay.fitnass.view.notification.NotificationController;
import com.akay.fitnass.util.Logger;

import org.threeten.bp.ZonedDateTime;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class FitService extends Service {
    public static final int FOREGROUND_SERVICE_ID = 1000;
    public static final String START_COMMAND = "COMMAND_START";
    public static final String PAUSE_COMMAND = "COMMAND_PAUSE";
    public static final String SAVE_COMMAND = "COMMAND_SAVE";
    public static final String LAP_COMMAND = "COMMAND_LAP";
    public static final String RESET_COMMAND = "COMMAND_RESET";

    @Inject RunsRepository mRepository;
    @Inject NotificationController mNotificationController;

    private ActiveRuns mActiveRuns;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("FitService: onCreate");
        App.getComponent().inject(this);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationController.getPersistentNotification());
        mRepository.getActiveRuns();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getAction();
        if (command == null) {
            return START_NOT_STICKY;
        }
        long ms = intent.getLongExtra("ms", 0);
        switch (command) {
            case START_COMMAND: start(ms); break;
            case PAUSE_COMMAND: pause(ms); break;
            case SAVE_COMMAND: save(ms); break;
            case LAP_COMMAND: lap(ms); break;
            case RESET_COMMAND: reset(); break;
            default: Logger.e("Unknown command: " + command);
        }
        return START_STICKY;
    }

    private void start(long ms) {
        mActiveRuns = get();
        if (mActiveRuns == null) {
            mActiveRuns = new ActiveRuns();
        }
        mActiveRuns.setPaused(false);
        mActiveRuns.setDateTime(ZonedDateTime.now());
        mActiveRuns.setStart(DateTimeUtils.fromMs(ms));
        mActiveRuns.setLaps(Collections.emptyList());
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void pause(long ms) {
        mActiveRuns = get();
        mActiveRuns.setPaused(true);
        mActiveRuns.setTws(DateTimeUtils.fromMs(ms));
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void save(long ms) {
        Logger.e("FitService: save");
        mActiveRuns = get();
        Runs runs = toSavedRuns(mActiveRuns);
        mActiveRuns = null;
        mRepository.saveRuns(runs);
        mRepository.deleteActiveRuns();
        stopSelf();
    }

    private void lap(long ms) {
        mActiveRuns = get();
        List<Lap> laps = mActiveRuns.getLaps();
        Lap lap = new Lap();
        lap.setTime(DateTimeUtils.fromMs(ms));
        laps.add(lap);
        mActiveRuns.setLaps(laps);
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void reset() {
        Logger.e("FitService: reset");
        mActiveRuns = null;
        mRepository.deleteActiveRuns();
        stopSelf();
    }

    private ActiveRuns get() {
        if (mActiveRuns == null) {
            mActiveRuns = mRepository.getActiveRuns().getValue();
        }
        return mActiveRuns;
    }

    private Runs toSavedRuns(final ActiveRuns activeRuns) {
        Runs runs = new Runs();
        runs.setDateTime(activeRuns.getDateTime());
        runs.setLaps(activeRuns.getLaps());
        return runs;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("FitService: onDestroy");
        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
