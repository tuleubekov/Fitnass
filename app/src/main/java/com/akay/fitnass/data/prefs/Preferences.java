package com.akay.fitnass.data.prefs;

public interface Preferences {

    void saveCurrentTime(long millis);

    long getCurrentTime();
}
