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
//        mNotificationController = SourceProvider.provideNotificationController(this);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationController.getPersistentNotification());
        mRepository.getActiveRuns();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e("FitService: onStartCommand");
        String command = intent.getAction();
        if (command == null) {
            return START_NOT_STICKY;
        }
        switch (command) {
            case START_COMMAND: start(); break;
            case PAUSE_COMMAND: pause(); break;
            case SAVE_COMMAND: save(); break;
            case LAP_COMMAND: lap(); break;
            case RESET_COMMAND: reset(); break;
            default: Logger.e("Unknown command: " + command);
        }
        return START_STICKY;
    }

    private void start() {
        Logger.e("FitService: start");
        mActiveRuns = get();
        if (mActiveRuns == null) {
            Logger.e("FitService: get new ActiveRuns");
            mActiveRuns = new ActiveRuns();
        }
        mActiveRuns.setPaused(false);
        mActiveRuns.setDateTime(ZonedDateTime.now());
        mActiveRuns.setStart(ZonedDateTime.now());
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void pause() {
        Logger.e("FitService: pause");
        mActiveRuns = get();
        mActiveRuns.setPaused(true);
        mActiveRuns.setTws(ZonedDateTime.now());
        mRepository.upsertActiveRuns(mActiveRuns);
    }

    private void save() {
        Logger.e("FitService: save");
        mActiveRuns = get();
        Runs runs = toSavedRuns(mActiveRuns);
        mActiveRuns = null;
        mRepository.saveRuns(runs);
        mRepository.deleteActiveRuns();
        stopSelf();
    }

    private void lap() {
        Logger.e("FitService: lap");
        mActiveRuns = get();
        List<Lap> laps = mActiveRuns.getLaps();
        Lap lap = new Lap();
        lap.setTime(ZonedDateTime.now());
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
            Logger.e("FitService: get activeRuns from db");
            mActiveRuns = mRepository.getActiveRuns().getValue();
        }
        Logger.e("FitService: get activeRuns");
        return mActiveRuns;
    }

    private Runs toSavedRuns(final ActiveRuns activeRuns) {
        Runs runs = new Runs();
        runs.setDateTime(activeRuns.getDateTime());
        runs.setLaps(activeRuns.getLaps());
        return runs;
    }

    //    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        String command = intent.getAction();
//        if (TextUtils.isEmpty(command)) {
//            Logger.e("Service: action command null or empty");
//            return START_STICKY;
//        }
//
//        switch (intent.getAction()) {
//            case "start_pause":
//                Logger.e("Service: action start_pause tapped");
//                startPauseAction();
//                break;
//            case "lap":
//                Logger.e("Service: action lap tapped");
//                onLapClicked();
//                break;
//            default: Logger.e("Service: action unknown");
//        }
//        return START_STICKY;
//    }
//
//    private void startPauseAction() {
//        ActiveRuns activeWorkout = mActiveWorkoutService.getActiveRuns();
//        boolean isPaused = activeWorkout.isPaused();
//        Logger.e("Service: startPauseAction clicked. ActiveWorkout state is paused: " + isPaused);
//
//        ActiveRuns activeWorkout1 = buildActiveRuns(!isPaused);
//        mActiveWorkoutService.upsert(activeWorkout1);
//        Logger.e("Service: ActiveWorkout saved new value. isPaused: " + activeWorkout1.isPaused());
//
//        mNotificationController.showStartPauseNotification(!activeWorkout1.isPaused());
//    }
//
//    private void onLapClicked() {
//        Logger.e("Click lap button");
//    }
//
//    private ActiveRuns buildActiveRuns(boolean isPaused) {
//        ActiveRuns activeWorkout = new ActiveRuns();
//        activeWorkout.setPaused(isPaused);
//
//        activeWorkout.setDateTime(ZonedDateTime.now());
//        return activeWorkout;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("FitService: ");
        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
