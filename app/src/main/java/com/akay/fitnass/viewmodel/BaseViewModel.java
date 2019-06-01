package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.model.ActiveRuns;

public class BaseViewModel extends ViewModel {
    private final RunsRepository mRepository;

    BaseViewModel() {
        mRepository = App.getComponent().getRepository();
    }

    protected RunsRepository getRepo() {
        return mRepository;
    }

    public LiveData<ActiveRuns> getLiveActiveRuns() {
        return getRepo().getLiveActiveRuns();
    }
}
