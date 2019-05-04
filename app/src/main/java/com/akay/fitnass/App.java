package com.akay.fitnass;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.akay.fitnass.data.db.AppDatabase;
import com.akay.fitnass.di.AppComponent;
import com.akay.fitnass.di.AppModule;
import com.akay.fitnass.di.DaggerAppComponent;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    private static final String DB_NAME = "FitnassDatabase";
    private static AppComponent mComponent;
    private AppDatabase mRoom;

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

    private static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static AppDatabase getDb(Context context) {
        return get(context).mRoom;
    }

    public static AppComponent getComponent() {
        return mComponent;
    }
}
