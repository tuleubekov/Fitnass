package com.akay.fitnass.viewmodel

import android.arch.lifecycle.LiveData

import com.akay.fitnass.data.model.ActiveRuns

class TimerViewModel : BaseViewModel() {

    val activeRuns: LiveData<ActiveRuns>
        get() = repo.getLiveActiveRuns()
}
