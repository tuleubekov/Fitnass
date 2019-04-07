package com.akay.fitnass.ui.main;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Workout;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutDayAdapter extends RecyclerView.Adapter<WorkoutDayAdapter.ViewHolder> {
    private static final String FORMAT_DATETIME = "dd MMMM yyyy HH:mm";
    private List<Workout> mWorkouts;
    private OnItemClickListener mItemClickListener;
    private int mSelected = -1;

    /*
     * Добавлено: при нажатии на итем изменяет его бэкграунд
     * Есть проблема - неприятная задержка в отклике нажатия
     * Проблема из-за фонового изображения
     * */

    WorkoutDayAdapter(List<Workout> workoutList, OnItemClickListener listener) {
        mWorkouts = workoutList;
        mItemClickListener = listener;
    }

    public void onWorkoutListUpdated(List<Workout> workouts) {
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
        Workout workout = mWorkouts.get(pos);
        DateTimeFormatter format = DateTimeFormat.forPattern(FORMAT_DATETIME);
        viewHolder.textDate.setText(format.print(workout.getDate()));
        viewHolder.textRunCount.setText(String.valueOf(workout.getCount()));
        viewHolder.layoutDay.setOnClickListener(view -> {
            mItemClickListener.onItemClicked(workout.getId());
            mSelected = viewHolder.getAdapterPosition();
//            notifyDataSetChanged();
        });
        if (mSelected == pos) {
            viewHolder.layoutDay.setSelected(true);
        } else {
            viewHolder.layoutDay.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_item_day) ConstraintLayout layoutDay;
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
