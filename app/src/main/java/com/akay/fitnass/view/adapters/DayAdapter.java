package com.akay.fitnass.view.adapters;

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

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private static final String FORMAT_DATETIME = "dd MMMM yyyy HH:mm";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME);

    private List<Runs> mWorkouts;
    private DayAdapter.OnItemClickListener mItemClickListener;
    private DayAdapter.OnItemLongClickListener mItemLongClickListener;

    public DayAdapter(List<Runs> workoutList, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        mWorkouts = workoutList;
        mItemClickListener = clickListener;
        mItemLongClickListener = longClickListener;
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
        viewHolder.textRunCount.setText(String.valueOf(workout.getLaps().size()));
    }

    @Override
    public synchronized int getItemCount() {
        return mWorkouts == null ? 0 : mWorkouts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_date) TextView textDate;
        @BindView(R.id.text_run_count) TextView textRunCount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> {
                Runs runs = mWorkouts.get(getLayoutPosition());
                mItemClickListener.onItemClicked(runs.getId());
            });
            itemView.setOnLongClickListener(view -> {
                Runs runs = mWorkouts.get(getLayoutPosition());
                mItemLongClickListener.onItemLongClicked(runs);
                return true;
            });
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClicked(long idRuns);
    }

    @FunctionalInterface
    public interface OnItemLongClickListener {
        void onItemLongClicked(Runs runs);
    }
}
