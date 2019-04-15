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
import com.akay.fitnass.ui.custom.Timer;
import com.akay.fitnass.ui.custom.CheckedButton;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutAddActivity extends AppCompatActivity {
    @BindView(R.id.chronometer) Timer mTimer;
    @BindView(R.id.recycler_workout) RecyclerView mRecyclerWorkoutLap;
    @BindView(R.id.btn_start_pause) CheckedButton mBtnStartPause;
    @BindView(R.id.btn_lap_save) CheckedButton mBtnLapSave;
    @BindView(R.id.btn_reset) Button mBtnReset;

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

    @OnClick(R.id.btn_start_pause)
    public void onStartClicked() {
        if (mBtnStartPause.isChecked()) {
            mTimer.pause();
            mBtnReset.setEnabled(true);
            mBtnLapSave.setEnabled(mAdapter.getItemCount() > 0);
        } else {
            mTimer.start();
            mBtnReset.setEnabled(false);
            mBtnLapSave.setEnabled(true);
        }
        mBtnStartPause.toggle();
        mBtnLapSave.toggle();
    }

    @OnClick(R.id.btn_reset)
    public void onResetClicked() {
        mAdapter.clear();
        mTimer.reset();
        mBtnReset.setEnabled(false);
        mBtnLapSave.setEnabled(false);
    }

    @OnClick(R.id.btn_lap_save)
    public void onLapClicked() {
        if (mBtnLapSave.isChecked()) {
            Lap lap = new Lap.Builder()
                    .setCircle(++circleCount)
                    .setLapTime(mTimer.getText().toString())
                    .build();
            mAdapter.addLap(lap);
        } else {
            Workout workout = new Workout();
            workout.setDate(DateTime.now().getMillis());
            workout.setType("run");
            workout.setLaps(mAdapter.getLaps());
            workout.setCount(mAdapter.getItemCount());
            mWorkoutService.insert(workout);
            finish();
        }
    }
}
