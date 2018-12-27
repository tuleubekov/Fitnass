package com.akay.fitnass.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.service.SourceProvider;
import com.akay.fitnass.service.WorkoutService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkout;
    @BindView(R.id.btn_add) Button mBtnNewDay;

    private WorkoutAdapter mAdapter;
    private WorkoutService mWorkoutService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mWorkoutService = SourceProvider.provideWorkoutService(this);
        mAdapter = new WorkoutAdapter(mWorkoutService.getAll());
        mRecyclerWorkout.setAdapter(mAdapter);
    }
}
