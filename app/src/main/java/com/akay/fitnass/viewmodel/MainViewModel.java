package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.LiveData;

import com.akay.fitnass.data.model.Runs;

import java.util.List;

public class MainViewModel extends BaseViewModel {
    private LiveData<List<Runs>> mRunsList;

    public MainViewModel() {
        mRunsList = getRepo().getLiveRuns();
    }

    public LiveData<List<Runs>> getLiveRunsList() {
        return mRunsList;
    }

}
