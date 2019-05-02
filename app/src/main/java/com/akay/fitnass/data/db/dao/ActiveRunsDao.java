package com.akay.fitnass.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.model.ActiveRuns;

@Dao
public abstract class ActiveRunsDao extends UpsertBaseDao<ActiveRuns> {

    @Query("SELECT * FROM active_runs WHERE id = 1")
    public abstract ActiveRuns getActiveRuns();
}
