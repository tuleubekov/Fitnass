package com.akay.fitnass.view.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.util.Logger;
import com.akay.fitnass.view.adapters.DayAdapter;
import com.akay.fitnass.view.custom.CheckedButton;
import com.akay.fitnass.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

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
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mAdapter = new DayAdapter(new ArrayList<>(), this::onItemClicked);
        mRecyclerWorkout.setAdapter(mAdapter);
        mViewModel.getRunsList().observe(this, this::onRunsListChanged);
    }

    @Override
    protected void initViewRxObservables() {
        addDisposables(initNewRunsObserver());
    }

    private void onRunsListChanged(final List<Runs> runs) {
        mAdapter.onWorkoutListUpdated(runs);
    }

    private void onAddRunsClicked(Object view) {
        startActivity(TimerActivity.getIntent(this));
    }

    private void onItemClicked(long idWorkout) {
        startActivity(DetailActivity.getIntent(this, idWorkout));
    }

    private Disposable initNewRunsObserver() {
        return clickObserver(mBtnNewDay).subscribe((this::onAddRunsClicked));
    }
}
