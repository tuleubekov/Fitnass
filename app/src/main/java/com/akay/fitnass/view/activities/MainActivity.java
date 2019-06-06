package com.akay.fitnass.view.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.ActiveRuns;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.view.adapters.DayAdapter;
import com.akay.fitnass.view.custom.CheckedButton;
import com.akay.fitnass.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

import static com.akay.fitnass.service.FitService.INIT_STATE_COMMAND;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recycler_day) RecyclerView mRecyclerWorkout;
    @BindView(R.id.btn_add) CheckedButton mBtnNewDay;

    private MainViewModel mViewModel;
    private DayAdapter mAdapter;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mAdapter = new DayAdapter(new ArrayList<>(), this::onItemClicked, this::onItemLongClicked);
        mRecyclerWorkout.setAdapter(mAdapter);
        mViewModel.getLiveRunsList().observe(this, this::onRunsListChanged);
        mViewModel.getLiveActiveRuns().observe(this, this::onActiveRunsChanged);
    }

    @Override
    protected void initViewRxObservables() {
        addDisposables(initNewRunsObserver());
    }

    private void onRunsListChanged(final List<Runs> runs) {
        mAdapter.onWorkoutListUpdated(runs);
    }

    private void onActiveRunsChanged(final ActiveRuns activeRuns) {
        boolean isNotNull = activeRuns != null;
        onRunsButtonState(isNotNull);
        if (isNotNull) {
            sendCommand(INIT_STATE_COMMAND);
        }
    }

    private void onRunsButtonState(boolean isNotActiveRuns) {
        mBtnNewDay.setChecked(isNotActiveRuns);
    }

    private void onAddRunsClicked(Object view) {
        startActivity(TimerActivity.getIntent(this));
    }

    private void onItemClicked(long idRuns) {
        startActivity(DetailActivity.getIntent(this, idRuns));
    }

    private void onItemLongClicked(Runs runs) {
        showDeleteRunsDialog((dialog, id) -> mViewModel.deleteDayRuns(runs));
    }

    private Disposable initNewRunsObserver() {
        return clickObserver(mBtnNewDay).subscribe((this::onAddRunsClicked));
    }
}
