package com.akay.fitnass.ui.workoutadd;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.storage.model.ActiveWorkout;
import com.akay.fitnass.data.storage.model.Lap;
import com.akay.fitnass.data.storage.model.TimerParams;
import com.akay.fitnass.data.storage.model.Workout;
import com.akay.fitnass.service.FitService;
import com.akay.fitnass.services.ActiveWorkoutService;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.services.WorkoutService;
import com.akay.fitnass.ui.custom.Timer;
import com.akay.fitnass.ui.custom.CheckedButton;

import org.joda.time.DateTime;

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
    private int circleCount;

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
        ActiveWorkout activeWorkout = mActiveWorkoutService.getActiveSession();
        if (activeWorkout == null) {
            return;
        }

        long start = activeWorkout.getStart();
        long tws = activeWorkout.getTimeWhenStopped();
        boolean started = activeWorkout.isStarted();
        boolean paused = activeWorkout.isPaused();

        TimerParams params = new TimerParams();
        params.setStart(start);
        params.setTws(tws);
        params.setStarted(started);
        params.setPaused(paused);
        mTimer.setParams(params);

        mAdapter.setLaps(activeWorkout.getLaps());
        mBtnStartPause.setChecked(!paused);
        mBtnReset.setEnabled(paused);
        mBtnLapSave.setChecked(!paused);

        if (mTimer.isPaused()) {
            mBtnLapSave.setEnabled(mAdapter.getItemCount() > 0);
        } else {
            mBtnLapSave.setEnabled(true);
        }

    }

    @OnClick(R.id.btn_start_pause)
    public void onStartClicked() {
        if (mBtnStartPause.isChecked()) {
            mTimer.pause();
            mBtnReset.setEnabled(true);
            mBtnLapSave.setEnabled(mAdapter.getItemCount() > 0);
        } else {
            mTimer.start();
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
        return new Lap.Builder()
                .setCircle(++circleCount)
                .setLapTime(mTimer.getText().toString())
                .build();
    }

    private Workout buildWorkout() {
        Workout workout = new Workout();
        workout.setDate(DateTime.now().getMillis());
        workout.setType("run");
        workout.setLaps(mAdapter.getLaps());
        workout.setCount(mAdapter.getItemCount());
        return workout;
    }

    private ActiveWorkout buildActiveWorkout() {
        TimerParams params = mTimer.getParams();
        ActiveWorkout activeWorkout = new ActiveWorkout();
        activeWorkout.setId(ActiveWorkout.ID);
        activeWorkout.setStart(params.getStart());
        activeWorkout.setTimeWhenStopped(params.getTws());
        activeWorkout.setStarted(params.isStarted());
        activeWorkout.setPaused(params.isPaused());
        activeWorkout.setLaps(mAdapter.getLaps());
        activeWorkout.setCount(mAdapter.getItemCount());
        activeWorkout.setDate(DateTime.now().getMillis());
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
