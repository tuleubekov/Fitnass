package com.akay.fitnass.data.storage.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Lap {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int circle;
    private String lapTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCircle() {
        return circle;
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }

    public String getLapTime() {
        return lapTime;
    }

    public void setLapTime(String lapTime) {
        this.lapTime = lapTime;
    }

    public static class Builder {
        private int circle;
        private String lapTime;

        public Builder setCircle(int circle) {
            this.circle = circle;
            return this;
        }

        public Builder setLapTime(String lapTime) {
            this.lapTime = lapTime;
            return this;
        }

        public Lap build() {
            Lap lap = new Lap();
            lap.setCircle(this.circle);
            lap.setLapTime(this.lapTime);
            return lap;
        }
    }
}
