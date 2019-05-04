package com.akay.fitnass.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Runs;
import com.akay.fitnass.view.main.WorkoutDayAdapter;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private static final String FORMAT_DATETIME = "dd MMMM yyyy HH:mm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME);

    private List<Runs> mWorkouts;
    private WorkoutDayAdapter.OnItemClickListener mItemClickListener;

    public DayAdapter(List<Runs> workoutList, WorkoutDayAdapter.OnItemClickListener listener) {
        mWorkouts = workoutList;
        mItemClickListener = listener;
    }

    public void onWorkoutListUpdated(List<Runs> workouts) {
        mWorkouts = workouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day, viewGroup, false);
        return new DayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.ViewHolder viewHolder, int pos) {
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
        @BindView(R.id.text_date)
        TextView textDate;
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
