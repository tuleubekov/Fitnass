package com.akay.fitnass.ui;

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

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    private List<Workout> mWorkouts;

    WorkoutAdapter(List<Workout> workoutList) {
        mWorkouts = workoutList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_workout_list, viewGroup, false);
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
            ButterKnife.bind(itemView);
        }
    }
}
