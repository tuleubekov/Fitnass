package com.akay.fitnass.data.storage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.storage.model.ActiveRuns;

@Dao
public abstract class ActiveRunsDao extends UpsertBaseDao<ActiveRuns> {

    @Query("SELECT * FROM active_runs WHERE id = 1")
    public abstract ActiveRuns getActiveRuns();
}
