package com.akay.fitnass.data

import androidx.lifecycle.LiveData

import com.akay.fitnass.data.model.ActiveRuns
import com.akay.fitnass.data.model.Runs

interface RunsRepository {

    fun getActiveRuns(): ActiveRuns

    fun getLiveActiveRuns(): LiveData<ActiveRuns>

    fun getLiveRuns(): LiveData<List<Runs>>

    fun getById(id: Long): Runs

    fun upsertActiveRuns(activeRuns: ActiveRuns)

    fun deleteActiveRuns()

    fun saveRuns(runs: Runs)

    fun deleteRuns(runs: Runs)
}
