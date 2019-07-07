package com.akay.fitnass.viewmodel

import androidx.lifecycle.LiveData

import com.akay.fitnass.data.model.Runs

class MainViewModel : BaseViewModel() {
    fun getLiveRunsList(): LiveData<List<Runs>> = repo.getLiveRuns()

    fun deleteDayRuns(runs: Runs) {
        repo.deleteRuns(runs)
    }
}
