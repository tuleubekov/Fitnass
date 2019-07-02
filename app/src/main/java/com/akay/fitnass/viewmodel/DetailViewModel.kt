package com.akay.fitnass.viewmodel

import com.akay.fitnass.data.model.Runs

class DetailViewModel : BaseViewModel() {

    fun getById(id: Long): Runs = repo.getById(id)
}
