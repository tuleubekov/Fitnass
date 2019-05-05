package com.akay.fitnass.view.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.view.adapters.LapAdapter;
import com.akay.fitnass.view.detail.WorkoutDetailActivity;
import com.akay.fitnass.viewmodel.DetailViewModel;

import butterknife.BindView;

public class DetailActivity extends BaseActivity {
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;

    public static final String RUNS_ID_KEY = "com.akay.fitnass.ui.detail.WORKOUT_ID_KEY";

    private DetailViewModel mViewModel;
    private LapAdapter mAdapter;

    public static Intent getIntent(Context context, long idWorkout) {
        Intent intent = new Intent(context, WorkoutDetailActivity.class);
        intent.putExtra(RUNS_ID_KEY, idWorkout);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        Runs runs = mViewModel.getById(getRunsId());
        mAdapter = new LapAdapter(runs.getLaps());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    private long getRunsId() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(RUNS_ID_KEY)) {
            throw new IllegalArgumentException("Workout ID is missing!");
        }
        return intent.getLongExtra(RUNS_ID_KEY, -1);
    }
}
