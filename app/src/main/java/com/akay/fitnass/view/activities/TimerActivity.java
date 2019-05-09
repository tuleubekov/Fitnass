package com.akay.fitnass.view.activities;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.service.FitService;
import com.akay.fitnass.util.DateTimeUtils;
import com.akay.fitnass.util.Logger;
import com.akay.fitnass.view.adapters.LapAdapter;
import com.akay.fitnass.view.custom.CheckedButton;
import com.akay.fitnass.view.custom.Timer;
import com.akay.fitnass.viewmodel.TimerViewModel;

import org.threeten.bp.ZonedDateTime;

import java.util.Collections;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

import static com.akay.fitnass.service.FitService.LAP_COMMAND;
import static com.akay.fitnass.service.FitService.PAUSE_COMMAND;
import static com.akay.fitnass.service.FitService.RESET_COMMAND;
import static com.akay.fitnass.service.FitService.SAVE_COMMAND;
import static com.akay.fitnass.service.FitService.START_COMMAND;

public class TimerActivity extends BaseActivity {
    @BindView(R.id.chronometer) Timer mTimer;
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;
    @BindView(R.id.btn_start_pause) CheckedButton mBtnStartPause;
    @BindView(R.id.btn_lap_save) CheckedButton mBtnLapSave;
    @BindView(R.id.btn_reset) Button mBtnReset;

    private TimerViewModel mViewModel;
    private LapAdapter mAdapter;
    private ActiveRuns mActiveRuns;

    public static Intent getIntent(Context context) {
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        mViewModel.getActiveRuns().observe(this, this::onActiveRunsChanged);
        mAdapter = new LapAdapter(Collections.emptyList());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    @Override
    protected void initViewRxObservables() {
        addDisposables(initStartPauseObserver());
        addDisposables(initLapSaveObserver());
        addDisposables(initResetObserver());
    }

    private void onActiveRunsChanged(ActiveRuns activeRuns) {
        mActiveRuns = activeRuns;
        if (mActiveRuns == null) {
            Logger.e("Act-ty: onActiveRunsChanged NULL!");
            return;
        }
        boolean isPaused = activeRuns.isPaused();
        long start = DateTimeUtils.toMs(mActiveRuns.getStart());
        long tws = DateTimeUtils.toMs(mActiveRuns.getTws());

        Logger.e("Act-ty: onActiveRunsChanged paused: " + isPaused + ", start: " + start + ", tws: " + tws);

//        handleActiveRuns(mActiveRuns);

        mAdapter.setLaps(activeRuns.getLaps());
        mTimer.setUp(isPaused, start, tws);

        if (isPaused) {
//            mTimer.setUp(isPaused, start, tws);

//            mTimer.pause(tws);
        } else {
//            mTimer.start(start);
        }
        uiSetUp(isPaused);
    }

    private void handleActiveRuns(final ActiveRuns activeRuns) {

    }

    private void onStartPauseClicked(Object view) {
        long now = nowMillis();
        if (mActiveRuns == null) {
            mTimer.start(now);
            sendCommand(START_COMMAND, now);
            return;
        }

        boolean isPaused = !mActiveRuns.isPaused();
        if (isPaused) {
            Logger.e("Act-ty: onStartPauseClicked: pause tws: " + now);
            mTimer.pause(now);
        } else {
            Logger.e("Act-ty: onStartPauseClicked: start ms: " + now);
            mTimer.start(now);
        }
        sendCommand(isPaused ? PAUSE_COMMAND : START_COMMAND, now);
    }

    private void onLapSaveClicked(Object view) {
        boolean isPaused = mActiveRuns.isPaused();
        sendCommand(isPaused ? SAVE_COMMAND : LAP_COMMAND, DateTimeUtils.nowMs());
    }

    private void onResetClicked(Object view) {
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
        mBtnLapSave.setEnabled(mAdapter != null && mAdapter.getItemCount() > 0);
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

    private void sendCommand(String command, long nowMs) {
        Intent intent = new Intent(this, FitService.class);
        intent.setAction(command);
        intent.putExtra("ms", nowMs);
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

    private long nowMillis() {
        return DateTimeUtils.nowMs();
    }
}
