package com.akay.fitnass.di

import com.akay.fitnass.data.RunsRepository
import com.akay.fitnass.service.FitService
import com.akay.fitnass.viewmodel.BaseViewModel

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = [AppModule::class, StorageModule::class])
interface AppComponent {

    val repository: RunsRepository

    fun inject(viewModel: BaseViewModel)

    fun inject(service: FitService)
}
