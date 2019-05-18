package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.LiveData;

import com.akay.fitnass.data.model.ActiveRuns;

public class TimerViewModel extends BaseViewModel {

    public LiveData<ActiveRuns> getActiveRuns() {
        return getRepo().getLiveActiveRuns();
    }
}
