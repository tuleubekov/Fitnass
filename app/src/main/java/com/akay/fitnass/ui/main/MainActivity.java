package com.akay.fitnass.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.service.SourceProvider;
import com.akay.fitnass.service.WorkoutService;
import com.akay.fitnass.ui.detail.WorkoutDetailActivity;
import com.akay.fitnass.ui.workoutadd.WorkoutAddActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_day) RecyclerView mRecyclerWorkout;
    @BindView(R.id.btn_add) Button mBtnNewDay;

    private WorkoutDayAdapter mAdapter;
    private WorkoutService mWorkoutService;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mWorkoutService = SourceProvider.provideWorkoutService(this);
        mAdapter = new WorkoutDayAdapter(mWorkoutService.getAll(), this::onItemClicked);
        mRecyclerWorkout.setAdapter(mAdapter);
        mBtnNewDay.setOnClickListener((view -> startActivity(WorkoutAddActivity.getIntent(this))));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.onWorkoutListUpdated(mWorkoutService.getAll());
    }

    private void onItemClicked(long idWorkout) {
        startActivity(WorkoutDetailActivity.getIntent(this, idWorkout));
    }
}
