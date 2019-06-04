package com.akay.fitnass;

import android.app.Application;

import com.akay.fitnass.di.AppComponent;
import com.akay.fitnass.di.AppModule;
import com.akay.fitnass.di.DaggerAppComponent;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    private static AppComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initDagger();
    }

    private void initDagger() {
        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getComponent() {
        return mComponent;
    }





}
