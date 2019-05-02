package com.akay.fitnass.services;

import com.akay.fitnass.data.model.ActiveRuns;

public interface ActiveWorkoutService {

    ActiveRuns getActiveRuns();

    void delete(ActiveRuns workout);

    void upsert(ActiveRuns workout);
}
