package com.akay.fitnass;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.akay.fitnass.data.db.AppDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    private static final String DB_NAME = "FitnassDatabase";
    private AppDatabase mRoom;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        mRoom = Room.databaseBuilder(this, AppDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    private static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static AppDatabase getDb(Context context) {
        return get(context).mRoom;
    }
}
