package com.akay.fitnass.di

import android.content.Context

import com.akay.fitnass.view.notification.NotificationController
import com.akay.fitnass.view.notification.NotificationControllerImpl

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
@Singleton
class AppModule(private val mContext: Context) {

    @Provides
    @Singleton
    internal fun provideContext(): Context = mContext

    @Provides
    @Singleton
    fun provideNotificationController(context: Context): NotificationController = NotificationControllerImpl(context)
}
