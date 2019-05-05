package com.akay.fitnass.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.akay.fitnass.App;
import com.akay.fitnass.data.RunsRepository;
import com.akay.fitnass.data.model.Runs;

import javax.inject.Inject;

public class DetailViewModel extends ViewModel {
    @Inject RunsRepository mRepository;

    public DetailViewModel() {
        App.getComponent().inject(this);
    }

    public Runs getById(long id) {
        return mRepository.getById(id);
    }
}
