package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.model.Runs;

import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {
    @Inject RunsRepository mRepository;

    private LiveData<List<Runs>> mRunsList;

    public MainViewModel() {
        App.getComponent().inject(this);
        mRunsList = mRepository.getRuns();
    }

    public LiveData<List<Runs>> getRunsList() {
        return mRunsList;
    }
}
