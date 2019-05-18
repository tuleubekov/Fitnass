package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;

import javax.inject.Inject;

public class BaseViewModel extends ViewModel {
    @Inject RunsRepository mRepository;

    public BaseViewModel() {
        App.getComponent().inject(this);
    }

    protected RunsRepository getRepo() {
        return mRepository;
    }
}
