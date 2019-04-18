package com.akay.fitnass.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesImpl implements Preferences {
    private static final String FILE_NAME = "FITNASS_PREFS";
    private static final String ACTIVE_TIME_MILLIS_KEY = "com.akay.fitnass.data.prefs.ACTIVE_TIME_MILLIS";

    private SharedPreferences mPrefs;

    public PreferencesImpl(Context context) {
        mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveCurrentTime(long millis) {
        operation(editor -> editor.putLong(ACTIVE_TIME_MILLIS_KEY, millis));
    }

    @Override
    public long getCurrentTime() {
        return mPrefs.getLong(ACTIVE_TIME_MILLIS_KEY, 0);
    }

    private void operation(PreferencesOperation operation) {
        SharedPreferences.Editor editor = mPrefs.edit();
        operation.put(editor);
        editor.apply();
    }

    @FunctionalInterface
    private interface PreferencesOperation {
        void put(SharedPreferences.Editor editor);
    }
}
