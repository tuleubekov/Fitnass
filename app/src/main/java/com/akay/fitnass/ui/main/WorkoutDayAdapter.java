package com.akay.fitnass.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Runs;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.ViewHolder> {
    private static final String FORMAT_DATETIME = "dd MMMM yyyy HH:mm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME);

    private List<Runs> mWorkouts;
    private OnItemClickListener mItemClickListener;

    WorkoutDayAdapter(List<Runs> workoutList, OnItemClickListener listener) {
        mWorkouts = workoutList;
        mItemClickListener = listener;
    }

    public void onWorkoutListUpdated(List<Runs> workouts) {
        mWorkouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        Runs workout = mWorkouts.get(pos);

        viewHolder.textDate.setText(workout.getDateTime().format(formatter));
        viewHolder.textRunCount.setText(String.valueOf(getItemCount()));
        viewHolder.itemView.setOnClickListener(view -> mItemClickListener.onItemClicked(workout.getId()));
    }

    @Override
    public synchronized int getItemCount() {
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

    public interface OnItemClickListener {

        void onItemClicked(long idWorkout);
    }
}
