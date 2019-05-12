package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.model.ActiveRuns;

import javax.inject.Inject;

public class TimerViewModel extends ViewModel {
    @Inject RunsRepository mRepository;

    public TimerViewModel() {
        App.getComponent().inject(this);
    }

    public LiveData<ActiveRuns> getActiveRuns() {
        return mRepository.getLiveActiveRuns();
    }
}
