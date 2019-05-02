package com.akay.fitnass.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.threeten.bp.ZonedDateTime;

@Entity
public class Lap {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private ZonedDateTime time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public static class Builder {
        private ZonedDateTime lapTime;

        public Builder setLapTime(ZonedDateTime lapTime) {
            this.lapTime = lapTime;
            return this;
        }

        public Lap build() {
            Lap lap = new Lap();
            lap.setTime(this.lapTime);
            return lap;
        }
    }
}
