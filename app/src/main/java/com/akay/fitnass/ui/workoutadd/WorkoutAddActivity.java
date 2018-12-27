package com.akay.fitnass.ui.workoutadd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Lap;
import com.akay.fitnass.data.model.Workout;
import com.akay.fitnass.service.SourceProvider;
import com.akay.fitnass.service.WorkoutService;
import com.akay.fitnass.ui.custom.FitChronometer;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutAddActivity extends AppCompatActivity {
    @BindView(R.id.chronometer) FitChronometer mChronometer;
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;
    @BindView(R.id.btn_start) Button mStart;
    @BindView(R.id.btn_pause) Button mPause;
    @BindView(R.id.btn_reset) Button mReset;
    @BindView(R.id.btn_lap) Button mLap;
    @BindView(R.id.btn_save) Button mSave;

    private WorkoutService mWorkoutService;
    private WorkoutLapAdapter mAdapter;
    private int circleCount;


    public static Intent getIntent(Context context) {
        return new Intent(context, WorkoutAddActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_add);
        ButterKnife.bind(this);

        mWorkoutService = SourceProvider.provideWorkoutService(this);
        mAdapter = new WorkoutLapAdapter(new ArrayList<>());
        mRecyclerWorkoutLap.setAdapter(mAdapter);
    }

    @OnClick(R.id.btn_start)
    public void onStartClicked() {
        mLap.setEnabled(true);
        mReset.setEnabled(false);
        mPause.setEnabled(true);
        mChronometer.start();
    }


    @OnClick(R.id.btn_pause)
    public void onPauseClicked() {
        mReset.setEnabled(true);
        mLap.setEnabled(false);
        mSave.setEnabled(true);
        mStart.setEnabled(false);
        mChronometer.pause();
    }


    @OnClick(R.id.btn_reset)
    public void onResetClicked() {
        mStart.setEnabled(true);
        mPause.setEnabled(false);
        mReset.setEnabled(false);
        mSave.setEnabled(false);
        mAdapter.clear();
        mChronometer.reset();
    }


    @OnClick(R.id.btn_lap)
    public void onLapClicked() {
        Lap lap = new Lap.Builder().setCircle(++circleCount).setLapTime(mChronometer.getText().toString()).build();
        mAdapter.addLap(lap);
    }

    @OnClick(R.id.btn_save)
    public void onSaveClicked() {
        Workout workout = new Workout();
        workout.setDate(DateTime.now().getMillis());
        workout.setType("run");
        workout.setLaps(mAdapter.getLaps());
        workout.setCount(mAdapter.getItemCount());
        mWorkoutService.insert(workout);
        finish();
    }
}
