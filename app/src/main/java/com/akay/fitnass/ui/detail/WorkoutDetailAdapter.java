package com.akay.fitnass.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.db.model.Lap;
import com.akay.fitnass.util.DateTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkoutDetailAdapter extends RecyclerView.Adapter<WorkoutDetailAdapter.ViewHolder> {
    private List<Lap> mLaps;

    public WorkoutDetailAdapter(List<Lap> laps) {
        this.mLaps = laps;
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
        long zdtMillis = lap.getTime().toInstant().toEpochMilli();
        viewHolder.textIterator.setText(String.valueOf(i+1));
        viewHolder.textLap.setText(DateTimeUtils.msToStrFormat(zdtMillis));
    }

    @Override
    public int getItemCount() {
        return mLaps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_iterator) TextView textIterator;
        @BindView(R.id.text_lapTime) TextView textLap;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
