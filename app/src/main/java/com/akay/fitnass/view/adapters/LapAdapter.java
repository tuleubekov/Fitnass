package com.akay.fitnass.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akay.fitnass.R;
import com.akay.fitnass.data.model.Lap;
import com.akay.fitnass.util.DateTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {
    private RecyclerView mRecycler;
    private List<Lap> mLaps;

    public LapAdapter(List<Lap> lapList) {
        mLaps = lapList;
    }

    public List<Lap> getLaps() {
        return mLaps;
    }

    public void setLaps(List<Lap> laps) {
        if (laps != null) {
            this.mLaps = laps;
            notifyDataSetChanged();
        }
    }

    public void addLap(Lap lap) {
        mLaps.add(lap);
        notifyDataSetChanged();
        mRecycler.smoothScrollToPosition(mLaps.size()-1);
    }

    public void clear() {
        mLaps.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecycler = null;
    }

    @NonNull
    @Override
    public LapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lap_workout, viewGroup, false);
        return new LapAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LapAdapter.ViewHolder viewHolder, int i) {
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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
