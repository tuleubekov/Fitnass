package com.akay.fitnass.data.db.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "active_workout")
public class ActiveWorkout {
    @Ignore
    public static long ID = 1L;

    @PrimaryKey
    private long id;
    private boolean started;
    private boolean paused;
    private long start;
    private long timeWhenStopped;
    private List<Lap> laps;
    private int count;
    private long date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getTimeWhenStopped() {
        return timeWhenStopped;
    }

    public void setTimeWhenStopped(long timeWhenStopped) {
        this.timeWhenStopped = timeWhenStopped;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
