package com.akay.fitnass.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.storage.model.ActiveRuns;
import com.akay.fitnass.services.ActiveWorkoutService;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.services.WorkoutService;
import com.akay.fitnass.ui.custom.CheckedButton;
import com.akay.fitnass.ui.detail.WorkoutDetailActivity;
import com.akay.fitnass.ui.workoutadd.WorkoutAddActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_day) RecyclerView mRecyclerWorkout;
    @BindView(R.id.btn_add) CheckedButton mBtnNewDay;

    private WorkoutDayAdapter mAdapter;
    private WorkoutService mWorkoutService;
    private ActiveWorkoutService mActiveWorkoutService;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mActiveWorkoutService = SourceProvider.provideActiveWorkoutService(this);
        mWorkoutService = SourceProvider.provideWorkoutService(this);
        mAdapter = new WorkoutDayAdapter(mWorkoutService.getAll(), this::onItemClicked);
        mRecyclerWorkout.setAdapter(mAdapter);
        mBtnNewDay.setOnClickListener((view -> startActivity(WorkoutAddActivity.getIntent(this))));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initStateBtn();
        mAdapter.onWorkoutListUpdated(mWorkoutService.getAll());
    }

    private void initStateBtn() {
        ActiveRuns workout = mActiveWorkoutService.getActiveRuns();
        mBtnNewDay.setChecked(workout != null);
    }

    private void onItemClicked(long idWorkout) {
        startActivity(WorkoutDetailActivity.getIntent(this, idWorkout));
    }
}
