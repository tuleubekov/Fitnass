package com.akay.fitnass.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

import com.akay.fitnass.data.model.ActiveRuns

@Dao
interface ActiveRunsDao : BaseDao<ActiveRuns> {

    @Query("SELECT * FROM active_runs WHERE id = 1")
    fun getActiveRuns(): ActiveRuns

    @Query("SELECT * FROM active_runs WHERE id = 1")
    fun getLiveActiveRuns(): LiveData<ActiveRuns>

    @Query("DELETE FROM active_runs")
    fun deleteActiveRuns()
}
