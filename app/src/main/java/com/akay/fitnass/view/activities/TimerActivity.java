package com.akay.fitnass.view.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Lap;
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
import static com.akay.fitnass.util.DateTimes.nowMillis;
import static com.akay.fitnass.util.DateTimes.toMs;

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

        mActiveRuns = activeRuns;
        uiSetUp(activeRuns.isPaused());
        onTimerActions(activeRuns);

        if (!mInitialized) {
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
            mTimer.pause(toMs(activeRuns.getTws()));
        } else {
            mTimer.start(toMs(activeRuns.getStart()));
        }
    }

    private void onStartPauseClicked(Object view) {
        mInitialized = true;
        if (mActiveRuns == null) {
            mTimer.start(nowMillis());
            sendCommand(START_COMMAND);
            return;
        }

        boolean isPaused = !mActiveRuns.isPaused();
        sendCommand(isPaused ? PAUSE_COMMAND : START_COMMAND);
    }

    private void onLapSaveClicked(Object view) {
        boolean isPaused = mActiveRuns.isPaused();
        sendCommand(isPaused ? SAVE_COMMAND : LAP_COMMAND);
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
        sendCommand(RESET_COMMAND);
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
        return longCLickObserver(mBtnReset).subscribe(this::onResetClicked);
    }

    private boolean lapsValid(List<Lap> laps) {
        return laps != null && !laps.isEmpty();
    }
}
