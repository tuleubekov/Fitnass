package com.akay.fitnass.di;

import android.content.Context;

import com.akay.fitnass.view.notification.NotificationController;
import com.akay.fitnass.view.notification.NotificationControllerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class AppModule {
    private final Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides @Singleton
    public NotificationController provideNotificationController(Context context) {
        return new NotificationControllerImpl(context);
    }
}
