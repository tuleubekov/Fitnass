package com.akay.fitnass.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.akay.fitnass.App
import com.akay.fitnass.data.RunsRepository
import com.akay.fitnass.data.model.ActiveRuns

open class BaseViewModel : ViewModel() {
    protected val repo: RunsRepository = App.component.repository

    fun getLiveActiveRuns(): LiveData<ActiveRuns> = repo.getLiveActiveRuns()

}
