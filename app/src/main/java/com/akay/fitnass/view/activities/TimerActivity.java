package com.akay.fitnass.view.activities;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Lap;
import com.akay.fitnass.service.FitService;
import com.akay.fitnass.util.DateTimeUtils;
import com.akay.fitnass.view.adapters.LapAdapter;
import com.akay.fitnass.view.custom.CheckedButton;
import com.akay.fitnass.view.custom.Timer;
import com.akay.fitnass.viewmodel.TimerViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

import static com.akay.fitnass.service.FitService.LAP_COMMAND;
import static com.akay.fitnass.service.FitService.PAUSE_COMMAND;
import static com.akay.fitnass.service.FitService.RESET_COMMAND;
import static com.akay.fitnass.service.FitService.SAVE_COMMAND;
import static com.akay.fitnass.service.FitService.START_COMMAND;
import static com.akay.fitnass.util.DateTimeUtils.toMs;

public class TimerActivity extends BaseActivity {
    @BindView(R.id.chronometer) Timer mTimer;
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;
    @BindView(R.id.btn_start_pause) CheckedButton mBtnStartPause;
    @BindView(R.id.btn_lap_save) CheckedButton mBtnLapSave;
    @BindView(R.id.btn_reset) Button mBtnReset;

    private TimerViewModel mViewModel;
    private LapAdapter mAdapter;
    private ActiveRuns mActiveRuns;
    private boolean mInitialized;

    public static Intent getIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        mViewModel.getActiveRuns().observe(this, this::onActiveRunsChanged);
        mAdapter = new LapAdapter(new ArrayList<>());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mInitialized = false;
    }

    @Override
    protected void initViewRxObservables() {
        addDisposables(initStartPauseObserver());
        addDisposables(initLapSaveObserver());
        addDisposables(initResetObserver());
    }

    private void onActiveRunsChanged(ActiveRuns activeRuns) {
        if (activeRuns == null) {
            return;
        }

        if (lapsValid(activeRuns.getLaps())) {
            mAdapter.setLaps(activeRuns.getLaps());
        }

        uiSetUp(activeRuns.isPaused());
        mActiveRuns = activeRuns;

        if (mInitialized) {
            onTimerActions(activeRuns);
        } else {
            setUpTimer(activeRuns);
        }
    }

    private void setUpTimer(ActiveRuns activeRuns) {
        mInitialized = true;
        boolean isPaused = activeRuns.isPaused();
        long start = toMs(activeRuns.getStart());
        long tws = toMs(activeRuns.getTws());

        if (isPaused) {
            mTimer.setUpPause(start, tws);
        } else {
            mTimer.setUpStart(start, tws);
        }
    }

    private void onTimerActions(ActiveRuns activeRuns) {
        if (activeRuns.isPaused()) {
            onPauseTimer(activeRuns);
        } else {
            onStartTimer(activeRuns);
        }
    }

    private void onStartTimer(ActiveRuns activeRuns) {
        mTimer.start(toMs(activeRuns.getStart()));
    }

    private void onPauseTimer(ActiveRuns activeRuns) {
        mTimer.pause(toMs(activeRuns.getTws()));
    }

    private void onStartPauseClicked(Object view) {
        long now = nowMillis();
        mInitialized = true;
        if (mActiveRuns == null) {
            mTimer.start(now);
            sendCommand(START_COMMAND, now);
            return;
        }

        boolean isPaused = !mActiveRuns.isPaused();
        sendCommand(isPaused ? PAUSE_COMMAND : START_COMMAND, now);
    }

    private void onLapSaveClicked(Object view) {
        boolean isPaused = mActiveRuns.isPaused();
        sendCommand(isPaused ? SAVE_COMMAND : LAP_COMMAND, nowMillis());
        if (isPaused) {
            finish();
        }
    }

    private void onResetClicked(Object view) {
        mInitialized = false;
        mActiveRuns = null;
        mAdapter.clear();
        mTimer.reset();
        mBtnReset.setEnabled(false);
        mBtnLapSave.setEnabled(false);
        sendCommand(RESET_COMMAND, 0);
    }

    private void uiSetUp(final boolean isPaused) {
        mBtnStartPause.setChecked(!isPaused);
        mBtnLapSave.setChecked(isPaused);
        mBtnReset.setEnabled(isPaused);
        mBtnLapSave.setChecked(!isPaused);
        mBtnLapSave.setEnabled(!isPaused || mAdapter != null && mAdapter.getItemCount() > 0);
    }

    private Disposable initStartPauseObserver() {
        return clickObserver(mBtnStartPause).subscribe(this::onStartPauseClicked);
    }

    private Disposable initLapSaveObserver() {
        return clickObserver(mBtnLapSave).subscribe(this::onLapSaveClicked);
    }

    private Disposable initResetObserver() {
        return clickObserver(mBtnReset).subscribe(this::onResetClicked);
    }

    private void sendCommand(String command, long msAction) {
        Intent intent = new Intent(this, FitService.class);
        intent.setAction(command);
        intent.putExtra("ms", msAction);
        if (!isServiceRunningInForeground()) {
            ContextCompat.startForegroundService(this, intent);
        } else {
            startService(intent);
        }
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

    private boolean lapsValid(List<Lap> laps) {
        return laps != null && !laps.isEmpty();
    }

    private long nowMillis() {
        return DateTimeUtils.nowMs();
    }
}
