package com.akay.fitnass.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.db.model.Runs;
import com.akay.fitnass.services.SourceProvider;
import com.akay.fitnass.services.WorkoutService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutDetailActivity extends AppCompatActivity {
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;

    public static final String WORKOUT_ID_KEY = "com.akay.fitnass.ui.detail.WORKOUT_ID_KEY";

    private WorkoutService mWorkoutService;
    private WorkoutDetailAdapter mAdapter;

    public static Intent getIntent(Context context, long idWorkout) {
        Intent intent = new Intent(context, WorkoutDetailActivity.class);
        intent.putExtra(WORKOUT_ID_KEY, idWorkout);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_workout);
        ButterKnife.bind(this);
        mWorkoutService = SourceProvider.provideWorkoutService(this);
        Runs workout = mWorkoutService.getById(getWorkoutId());
        mAdapter = new WorkoutDetailAdapter(workout.getLaps());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    private long getWorkoutId() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(WORKOUT_ID_KEY)) {
            throw new IllegalArgumentException("Workout ID is missing!");
        }
        return intent.getLongExtra(WORKOUT_ID_KEY, -1);
    }
}
