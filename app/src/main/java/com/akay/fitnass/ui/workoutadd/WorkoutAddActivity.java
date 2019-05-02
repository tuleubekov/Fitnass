package com.akay.fitnass.ui.workoutadd;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.db.model.ActiveRuns;
import com.akay.fitnass.data.db.model.Lap;
import com.akay.fitnass.data.db.model.Runs;
import com.akay.fitnass.service.FitService;
import com.akay.fitnass.services.ActiveWorkoutService;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.services.WorkoutService;
import com.akay.fitnass.ui.custom.Timer;
import com.akay.fitnass.ui.custom.CheckedButton;
import com.akay.fitnass.util.DateTimeUtils;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class WorkoutAddActivity extends AppCompatActivity {
    @BindView(R.id.chronometer) Timer mTimer;
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;
    @BindView(R.id.btn_start_pause) CheckedButton mBtnStartPause;
    @BindView(R.id.btn_lap_save) CheckedButton mBtnLapSave;
    @BindView(R.id.btn_reset) Button mBtnReset;

    private WorkoutService mWorkoutService;
    private ActiveWorkoutService mActiveWorkoutService;
    private WorkoutLapAdapter mAdapter;
    private ActiveRuns mActiveRuns;

    public static Intent getIntent(Context context) {
        return new Intent(context, WorkoutAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_add);
        ButterKnife.bind(this);

        mWorkoutService = SourceProvider.provideWorkoutService(this);
        mActiveWorkoutService = SourceProvider.provideActiveWorkoutService(this);
        mAdapter = new WorkoutLapAdapter(new ArrayList<>());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupActiveWorkout();
    }

    private void setupActiveWorkout() {
        mActiveRuns = mActiveWorkoutService.getActiveRuns();
        if (mActiveRuns == null) {
            return;
        }

        long start = mActiveRuns.getStart().toInstant().toEpochMilli();
        long tws = mActiveRuns.getTws().toInstant().toEpochMilli();
//        boolean started = activeWorkout.isStarted();
        boolean paused = mActiveRuns.isPaused();
//
//        TimerParams params = new TimerParams();
//        params.setStart(start);
//        params.setTws(tws);
////        params.setStarted(started);
//        params.setPaused(paused);
//        mTimer.setParams(params);


        mTimer.prepare(new Timer.Builder()
                .setPaused(paused)
                .setStart(mActiveRuns.getStart())
                .setTws(mActiveRuns.getTws()));

        mAdapter.setLaps(mActiveRuns.getLaps());
        mBtnStartPause.setChecked(!paused);
        mBtnReset.setEnabled(paused);
        mBtnLapSave.setChecked(!paused);

        if (mTimer.isPaused()) {
            mBtnLapSave.setEnabled(mAdapter.getItemCount() > 0);
        } else {
            mBtnLapSave.setEnabled(true);
        }

        mTimer.update();
    }

    @OnClick(R.id.btn_start_pause)
    public void onStartClicked() {
        if (mBtnStartPause.isChecked()) {
//        mTimeWhenStopped = mStart - nowMillis();
            mActiveRuns.setTws(ZonedDateTime.now());
            mTimer.pause(DateTimeUtils.toMs(mActiveRuns.getTws()));
            mBtnReset.setEnabled(true);
            mBtnLapSave.setEnabled(mAdapter.getItemCount() > 0);
        } else {
//        mStart = nowMillis() + mTimeWhenStopped;
            if (mActiveRuns == null) {
                mActiveRuns = buildActiveWorkout();
            }
            mActiveRuns.setStart(ZonedDateTime.now());
            mTimer.start(DateTimeUtils.toMs(mActiveRuns.getStart()));
            mBtnReset.setEnabled(false);
            mBtnLapSave.setEnabled(true);
            startSocket();
        }
        mActiveWorkoutService.upsert(buildActiveWorkout());
        mBtnStartPause.toggle();
        mBtnLapSave.toggle();
    }

    @OnLongClick(R.id.btn_reset)
    public boolean onResetClicked() {
        mAdapter.clear();
        mTimer.reset();
        mBtnReset.setEnabled(false);
        mBtnLapSave.setEnabled(false);
        mActiveWorkoutService.delete(buildActiveWorkout());
        stopSocket();
        return true;
    }

    @OnClick(R.id.btn_lap_save)
    public void onLapClicked() {
        if (mBtnLapSave.isChecked()) {
            mAdapter.addLap(buildLap());
            mActiveWorkoutService.upsert(buildActiveWorkout());
        } else {
            mActiveWorkoutService.delete(buildActiveWorkout());
            mWorkoutService.insert(buildWorkout());
            stopSocket();
            finish();
        }
    }

    private Lap buildLap() {
//        long start =
        long nowMs = ZonedDateTime.now().toInstant().toEpochMilli();
        long lapMs = nowMs - DateTimeUtils.toMs(mActiveRuns.getStart());
        Instant instant = Instant.ofEpochMilli(lapMs);
        ZonedDateTime lapZdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return new Lap.Builder()
//                .setCircle(++circleCount)
                .setLapTime(lapZdt)
                .build();
    }

    private Runs buildWorkout() {
        Runs workout = new Runs();
        workout.setDateTime(ZonedDateTime.now());
        workout.setComment("comment:run");
        workout.setLaps(mAdapter.getLaps());
//        workout.setCount(mAdapter.getItemCount());
        return workout;
    }

    private ActiveRuns buildActiveWorkout() {
        //        mStart = nowMillis() + mTimeWhenStopped;
        //        mTimeWhenStopped = mStart - nowMillis();

//        TimerParams params = mTimer.getParams();
        ActiveRuns activeWorkout = new ActiveRuns();

        long start = DateTimeUtils.nowMs();
        long tws = start - DateTimeUtils.nowMs();

        activeWorkout.setStart(ZonedDateTime.now());
//        activeWorkout.setStarted(params.isStarted());
//        activeWorkout.setCount(mAdapter.getItemCount());

        activeWorkout.setTws(ZonedDateTime.now());
        activeWorkout.setPaused(mTimer.isPaused());
        activeWorkout.setLaps(mAdapter.getLaps());
        activeWorkout.setDateTime(ZonedDateTime.now());
        return activeWorkout;
    }

    private void startSocket() {
        Intent intent = new Intent(this, FitService.class);
        if (!isServiceRunningInForeground()) {
            ContextCompat.startForegroundService(this, intent);
        }
    }

    private void stopSocket() {
        Intent intent = new Intent(this, FitService.class);
        stopService(intent);
    }

    private boolean isServiceRunningInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FitService.class.getName().equals(service.service.getClassName())) {
                return service.foreground;
            }
        }
        return false;
    }
}
