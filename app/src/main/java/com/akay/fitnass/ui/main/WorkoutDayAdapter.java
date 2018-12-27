package com.akay.fitnass.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Workout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.ViewHolder> {
    private List<Workout> mWorkouts;

    WorkoutDayAdapter(List<Workout> workoutList) {
        mWorkouts = workoutList;
    }

    public void onWorkoutListUpdated(List<Workout> workouts) {
        mWorkouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Workout workout = mWorkouts.get(i);
        viewHolder.textDate.setText(String.valueOf(workout.getDate()));
        viewHolder.textRunCount.setText(String.valueOf(workout.getCount()));
    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_date) TextView textDate;
        @BindView(R.id.text_run_count) TextView textRunCount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
