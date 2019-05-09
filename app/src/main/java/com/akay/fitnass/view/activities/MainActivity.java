package com.akay.fitnass.view.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.util.Logger;
import com.akay.fitnass.view.adapters.DayAdapter;
import com.akay.fitnass.view.custom.CheckedButton;
import com.akay.fitnass.viewmodel.MainViewModel;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recycler_day) RecyclerView mRecyclerWorkout;
    @BindView(R.id.btn_add) CheckedButton mBtnNewDay;

    private MainViewModel mViewModel;
    private DayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mAdapter = new DayAdapter(Collections.emptyList(), this::onItemClicked);
        mRecyclerWorkout.setAdapter(mAdapter);
        mViewModel.getRunsList().observe(this, this::onRunsListChanged);
    }

    @Override
    protected void initViewRxObservables() {
        Logger.e("initViewRxObservables 2");
        addDisposables(initNewRunsObserver());
    }

    private void onRunsListChanged(final List<Runs> runs) {
        Logger.e("onRunsListChanged: count: " + runs.size());
        mAdapter.onWorkoutListUpdated(runs);
    }

    private void onAddRunsClicked(Object view) {
        Logger.e("onAddRunsClicked");
        startActivity(TimerActivity.getIntent(this));
    }

    private void onItemClicked(long idWorkout) {
        Logger.e("onAddRunsClicked");
        startActivity(DetailActivity.getIntent(this, idWorkout));
    }

    private Disposable initNewRunsObserver() {
        Logger.e("initNewRunsObserver");
        return clickObserver(mBtnNewDay).subscribe((this::onAddRunsClicked));
    }
}
