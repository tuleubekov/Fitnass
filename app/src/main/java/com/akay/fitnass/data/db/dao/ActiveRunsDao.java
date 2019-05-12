package com.akay.fitnass.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.akay.fitnass.data.model.ActiveRuns;

@Dao
public interface ActiveRunsDao extends BaseDao<ActiveRuns> {

    @Query("SELECT * FROM active_runs WHERE id = 1")
    ActiveRuns getActiveRuns();

    @Query("SELECT * FROM active_runs WHERE id = " + ActiveRuns.ID)
    LiveData<ActiveRuns> getLiveActiveRuns();

    @Query("DELETE FROM active_runs")
    void deleteActiveRuns();
}
