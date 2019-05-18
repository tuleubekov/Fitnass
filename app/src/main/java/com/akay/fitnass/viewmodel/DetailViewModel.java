package com.akay.fitnass.viewmodel;

import com.akay.fitnass.data.model.Runs;

public class DetailViewModel extends BaseViewModel {

    public Runs getById(long id) {
        return getRepo().getById(id);
    }
}
