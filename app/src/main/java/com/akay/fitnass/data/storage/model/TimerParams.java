package com.akay.fitnass.data.storage.model;

public class TimerParams {
    private long start;
    private long tws;
    private boolean started;
    private boolean paused;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getTws() {
        return tws;
    }

    public void setTws(long tws) {
        this.tws = tws;
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
}
