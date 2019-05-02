package com.akay.fitnass.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.threeten.bp.ZonedDateTime;

import java.util.List;

@Entity(tableName = "active_runs")
public class ActiveRuns {
    @Ignore
    public static final long ID = 1L;

    @PrimaryKey
    private long id;
    private boolean paused;
    private List<Lap> laps;
    private ZonedDateTime start;
    private ZonedDateTime tws;
    private ZonedDateTime dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getTws() {
        return tws;
    }

    public void setTws(ZonedDateTime tws) {
        this.tws = tws;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
