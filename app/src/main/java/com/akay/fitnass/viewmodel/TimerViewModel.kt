package com.akay.fitnass.viewmodel

import androidx.lifecycle.LiveData

import com.akay.fitnass.data.model.ActiveRuns

class TimerViewModel : BaseViewModel() {

    val activeRuns: LiveData<ActiveRuns>
        get() = repo.getLiveActiveRuns()
}
