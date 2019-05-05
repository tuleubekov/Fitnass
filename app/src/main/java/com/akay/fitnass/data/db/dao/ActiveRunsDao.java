package com.akay.fitnass.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.model.ActiveRuns;

@Dao
public abstract class ActiveRunsDao extends UpsertBaseDao<ActiveRuns> {

    @Query("SELECT * FROM active_runs WHERE id = 1")
    public abstract ActiveRuns getActiveRuns();

    @Query("SELECT * FROM active_runs WHERE id = " + ActiveRuns.ID)
    public abstract LiveData<ActiveRuns> getLiveActiveRuns();

    @Query("DELETE FROM active_runs")
    public abstract void deleteActiveRuns();
}
