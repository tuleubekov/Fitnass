package com.akay.fitnass.ui.workoutadd;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Lap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutLapAdapter extends RecyclerView.Adapter<WorkoutLapAdapter.ViewHolder> {
    private List<Lap> mLaps;

    WorkoutLapAdapter(List<Lap> lapList) {
        mLaps = lapList;
    }

    public List<Lap> getLaps() {
        return mLaps;
    }

    public void addLap(Lap lap) {
        mLaps.add(lap);
        notifyDataSetChanged();
    }

    public void clear() {
        mLaps.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lap_workout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Lap lap = mLaps.get(i);
        viewHolder.textIterator.setText(String.valueOf(lap.getCircle()));
        viewHolder.textLap.setText(lap.getLapTime());
    }

    @Override
    public int getItemCount() {
        return mLaps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_iterator) TextView textIterator;
        @BindView(R.id.text_lapTime) TextView textLap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
