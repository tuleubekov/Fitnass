package com.akay.fitnass

import android.app.Application

import com.akay.fitnass.di.AppComponent
import com.akay.fitnass.di.AppModule
import com.akay.fitnass.di.DaggerAppComponent
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initDagger()
    }

    private fun initDagger() {
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {
        lateinit var component: AppComponent
            private set
    }

}
