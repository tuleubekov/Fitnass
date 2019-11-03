package com.akay.fitnass

import android.app.Application
import androidx.multidex.MultiDex

import com.akay.fitnass.di.AppComponent
import com.akay.fitnass.di.AppModule
import com.akay.fitnass.di.DaggerAppComponent
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setup()
        initDagger()
    }

    private fun setup() {
        MultiDex.install(this)
        AndroidThreeTen.init(this)
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
